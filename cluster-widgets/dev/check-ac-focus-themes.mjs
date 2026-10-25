import assert from 'node:assert/strict';
import fs from 'node:fs';
import vm from 'node:vm';
const read = path => fs.readFileSync(new URL('../' + path, import.meta.url), 'utf8');
const helper = await import('../source/v1.0/shared/utils/clusterAcFocus.js');
const minimalist = read('source/v1.0/minimalist/src/core/main.js');
const component = read('source/v1.0/minimalist/src/core/components/aircon/mainAcControl.js');
const handler = minimalist.slice(minimalist.indexOf('function handleAirconKey('), minimalist.indexOf('function handleSteeringWheelKey('));
let checks = 0;
for (const power of [0, 1, undefined, 'unknown']) {
    const state = { focusArea: 'temp', power, temp: 22, fan: 3 };
    const hvac = []; const saved = [];
    const context = { get: key => state[key], setState: (key, value) => state[key] = value,
        androidUpdateCarData: (...args) => hvac.push(args), acFocusPreference: { save: value => saved.push(value) },
        acFocusTouched: false, window: {}, console };
    vm.createContext(context); vm.runInContext(handler, context);
    vm.runInContext("handleAirconKey('ENTER')", context);
    assert.equal(state.focusArea, 'fan'); assert.deepEqual(saved, ['fan']); assert.equal(hvac.length, 0);
    vm.runInContext("handleAirconKey('ENTER')", context);
    assert.equal(state.focusArea, 'temp'); assert.equal(hvac.length, 0);
    vm.runInContext("handleAirconKey('UP')", context);
    assert.deepEqual(hvac, [['car.hvac.driver_temperature', '22.5']]);
    checks++;
}
// Actual component power and mount paths must preserve both saved selections.
const powerBlock = component.slice(component.indexOf('    const applyPowerUi ='), component.indexOf("    unsubs.push(subscribe('power'"));
const mountBlock = component.slice(component.indexOf('    const onMount ='), component.indexOf('    const cleanup ='));
for (const focus of ['fan', 'temp']) {
    const state = { focusArea: focus };
    const element = { textContent: '', classList: { toggle() {} } };
    vm.runInNewContext(powerBlock + mountBlock + '\napplyPowerUi(0); applyPowerUi(undefined); onMount();', {
        powerPill: element, panel: element, stateManager: { get: key => state[key] },
        setState: (key, value) => state[key] = value
    });
    assert.equal(state.focusArea, focus); checks++;
}
// Startup/late-development-mock ordering, intervening selection and cleanup.
const init = minimalist.slice(minimalist.indexOf('const acFocusPreference ='), minimalist.indexOf('initializeConstants();'));
for (const touched of [false, true]) {
    const listeners = new Map(); const state = {}; let saves = 0;
    const window = { addEventListener: (event, fn) => listeners.set(event, fn),
        removeEventListener: event => listeners.delete(event) };
    const context = { window, setState: (key, value) => state[key] = value,
        createAcFocusPreference: getter => helper.createAcFocusPreference(getter, () => {}) };
    vm.createContext(context); vm.runInContext(init, context);
    assert.equal(state.focusArea, 'fan');
    if (touched) { vm.runInContext('acFocusTouched = true', context); state.focusArea = 'fan'; }
    window.Android = { __themeLabMock: 'shared', getClusterAcFocus: () => 'temp',
        saveClusterAcFocus: () => { saves++; return true; } };
    listeners.get('cluster-ac-focus-bridge-ready')();
    assert.equal(state.focusArea, touched ? 'fan' : 'temp');
    assert.equal(listeners.size, 0); assert.equal(saves, 0); checks++;
}
// Regression: exercise the REAL arrow handler before the mock arrives, not a manual flag.
for (const key of ['UP', 'DOWN']) {
    const listeners = new Map(); const state = { fan: 3, temp: 22, power: 1 }; const hvac = [];
    const window = { addEventListener: (event, fn) => listeners.set(event, fn),
        removeEventListener: event => listeners.delete(event) };
    const context = { window, console, get: name => state[name],
        setState: (name, value) => state[name] = value,
        androidUpdateCarData: (...args) => hvac.push(args),
        createAcFocusPreference: getter => helper.createAcFocusPreference(getter, () => {}) };
    vm.createContext(context); vm.runInContext(init + '\n' + handler, context);
    vm.runInContext(`handleAirconKey('${key}')`, context);
    assert.equal(hvac[0][0], 'car.hvac.fan_speed');
    window.Android = { __themeLabMock: 'shared', getClusterAcFocus: () => 'temp',
        saveClusterAcFocus: () => { throw Error('rehydration must not save'); } };
    listeners.get('cluster-ac-focus-bridge-ready')();
    assert.equal(state.focusArea, 'fan');
    vm.runInContext(`handleAirconKey('${key}')`, context);
    assert.equal(hvac[1][0], 'car.hvac.fan_speed');
    assert.equal(listeners.size, 0); checks++;
}
assert(minimalist.includes("window.removeEventListener('cluster-ac-focus-bridge-ready', rehydrateMockAcFocus);"));
for (const [name, version] of [['default', '1.4.44'], ['minimalist', '1.0.4'], ['ApexGT', '1.0.16']]) {
    const manifest = read(`source/v1.0/${name}/theme.xml`);
    assert(manifest.includes(`<version>${version}</version>`));
    assert(manifest.includes('<minBridgeVersion>1.0.2</minBridgeVersion>'));
}
console.log(`Global AC focus theme integration: ${checks} scenarios passed; three manifests verified.`);
