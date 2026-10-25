package br.com.redesurftank.havalshisuku.managers;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import static br.com.redesurftank.havalshisuku.managers.SteeringWheelNativeConfigPolicy.*;

public class SteeringWheelNativeConfigPolicyExtendedTest {
    static final class FakeStore implements SteeringWheelNativeConfigPolicy.Store {
        String current, backup, race;
        boolean changeOnSecondRead, commitFails, writeFails, commitThrows;
        int reads, commits, writes;
        final List<String> events = new ArrayList<>();
        FakeStore(String current, String backup) { this.current = current; this.backup = backup; }
        @Override public String readCurrent() {
            reads++; events.add("read");
            if (changeOnSecondRead && reads == 2) current = race;
            return current == null ? null : current.trim();
        }
        @Override public String readBackup() { return backup; }
        @Override public void saveBackup(String value) throws Exception {
            if (value.equals(readBackup())) return;
            if (!commitBackup(value)) throw new IllegalStateException("Could not persist native steering choice");
        }
        @Override public void writeCurrent(String value) throws Exception {
            putCurrent(value);
            if (!value.equals(readCurrent())) throw new IllegalStateException("Native steering setting write not confirmed");
        }
        private boolean commitBackup(String value) throws Exception {
            commits++; events.add("commit:" + value);
            if (commitThrows) throw new Exception("storage unavailable");
            if (commitFails) return false;
            backup = value; return true;
        }
        private void putCurrent(String value) {
            writes++; events.add("write:" + value);
            if (!writeFails) current = value;
        }
    }

    @Test public void nativeValuesMatchIntegerContract() {
        for (String value : new String[]{"-1", "0", "98", "100", "2147483647"})
            assertEquals(value, nativeValue(" " + value + "\n"));
        assertEquals("1", nativeValue("+001"));
        assertEquals("0", nativeValue("-0"));
        for (String value : new String[]{null, "", "null", "undefined", "99", "099", "-2",
                "2147483648", "1.0", "1 2", "a"}) assertNull(nativeValue(value));
    }

    @Test public void onlyEnabledCustomShortCaptures() {
        assertTrue(shouldCapture(true, true));
        assertFalse(shouldCapture(true, false)); // DOUBLE/LONG alone does not change this input
        assertFalse(shouldCapture(false, true));
        assertFalse(shouldCapture(false, false));
    }

    @Test public void liveNativeChoiceReplacesStaleBackupWithoutSystemWrite() throws Exception {
        FakeStore s = new FakeStore("100", "2");
        assertEquals(Result.PRESERVED, reconcile(s, false));
        assertEquals("100", s.backup); assertEquals("100", s.current); assertEquals(0, s.writes);
    }

    @Test public void captureCommitsLiveChoiceThenRereadsThenWritesAndConfirms() throws Exception {
        FakeStore s = new FakeStore("-1", "2");
        assertEquals(Result.CAPTURED, reconcile(s, true));
        assertEquals("-1", s.backup); assertEquals("99", s.current);
        assertEquals(Arrays.asList("read", "commit:-1", "read", "write:99", "read"), s.events);
    }

    @Test public void unchangedBackupAvoidsCommit() throws Exception {
        FakeStore s = new FakeStore("0", "0"); s.commitFails = true;
        assertEquals(Result.CAPTURED, reconcile(s, true)); assertEquals(0, s.commits);
    }

    @Test public void commitFailurePreventsAnyCaptureWrite() throws Exception {
        FakeStore s = new FakeStore("98", "1"); s.commitFails = true;
        try { reconcile(s, true); fail("commit failure must propagate"); }
        catch (IllegalStateException expected) { assertTrue(expected.getMessage().contains("persist")); }
        assertEquals(0, s.writes); assertEquals("98", s.current);
    }

    @Test public void commitExceptionPreventsCaptureWrite() throws Exception {
        FakeStore s = new FakeStore("100", null); s.commitThrows = true;
        try { reconcile(s, true); fail("commit exception must propagate"); }
        catch (Exception expected) { assertEquals("storage unavailable", expected.getMessage()); }
        assertEquals(0, s.writes);
    }

    @Test public void nativeChoiceChangedDuringCaptureIsNotOverwritten() throws Exception {
        for (String changed : new String[]{"3", "99", "garbage", null}) {
            FakeStore s = new FakeStore("2", "1"); s.changeOnSecondRead = true; s.race = changed;
            assertEquals(Result.CHANGED, reconcile(s, true)); assertEquals(0, s.writes);
            assertEquals("2", s.backup); // backup reflects first proven live native choice
        }
    }

    @Test public void canonicalEquivalentSecondReadStillCaptures() throws Exception {
        FakeStore s = new FakeStore("2", "1"); s.changeOnSecondRead = true; s.race = " +002 ";
        assertEquals(Result.CAPTURED, reconcile(s, true));
    }

    @Test public void alreadyCapturedDoesNotOverwriteBackup() throws Exception {
        FakeStore s = new FakeStore(" 99 ", "100");
        assertEquals(Result.ALREADY_CAPTURED, reconcile(s, true));
        assertEquals("100", s.backup); assertEquals(0, s.commits); assertEquals(0, s.writes);
    }

    @Test public void capturedWithUnknownBackupDoesNotGuess() throws Exception {
        for (String backup : new String[]{null, "99", "-2", "bad", "2147483648"}) {
            FakeStore s = new FakeStore("99", backup);
            assertEquals(Result.UNKNOWN, reconcile(s, false)); assertEquals(0, s.writes);
        }
    }

    @Test public void unknownCurrentNeverWritesEvenWithValidBackup() throws Exception {
        for (String current : new String[]{null, "", "null", "099", "+99", "-2", "bad"}) {
            for (boolean capture : new boolean[]{false, true}) {
                FakeStore s = new FakeStore(current, "100");
                assertEquals(Result.UNKNOWN, reconcile(s, capture));
                assertEquals(0, s.writes); assertEquals(0, s.commits);
            }
        }
    }

    @Test public void restoreAcceptsMinusOneAndAbove99AndCanonicalizesBackup() throws Exception {
        for (String backup : new String[]{"-1", "0", "98", "100"}) {
            FakeStore s = new FakeStore("99", " " + backup + " ");
            assertEquals(Result.RESTORED, reconcile(s, false)); assertEquals(backup, s.current);
            assertEquals(Arrays.asList("read", "read", "write:" + backup, "read"), s.events);
        }
    }

    @Test public void restoreRaceNeverOverwritesNewNativeChoice() throws Exception {
        for (String changed : new String[]{"3", "garbage", null, "099"}) {
            FakeStore s = new FakeStore("99", "1"); s.changeOnSecondRead = true; s.race = changed;
            assertEquals(Result.CHANGED, reconcile(s, false)); assertEquals(0, s.writes);
        }
    }

    @Test public void unconfirmedCaptureOrRestoreThrows() throws Exception {
        for (boolean capture : new boolean[]{false, true}) {
            FakeStore s = new FakeStore(capture ? "2" : "99", "1"); s.writeFails = true;
            try { reconcile(s, capture); fail("read-back mismatch must propagate"); }
            catch (IllegalStateException expected) { assertTrue(expected.getMessage().contains("not confirmed")); }
            assertEquals(1, s.writes);
        }
    }
}
