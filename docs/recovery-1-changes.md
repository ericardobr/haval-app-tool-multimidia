# Recovery.1 — alterações em relação à versão oficial

## Referência e escopo

Comparação com a base oficial **1.0.0.89**, commit
`a7533456bc0f0a4ee751e8bf66c092d59f5fd0e1` de `bobaoapae/haval-app-tool-multimidia`.
Esta branch publica somente registros documentais; não contém, por si só, a implementação das mudanças descritas.
Este documento descreve o código efetivamente incluído no APK assinado
**`1.0.0.89-acfocus-oemkeys-recovery.1`**, `versionCode=2`, e identifica separadamente
os temas complementares e as operações de instalação. Não é uma comparação com
a versão mais recente do upstream, nem uma alegação de reprodução byte a byte da antiga test.2.

## Alterações no aplicativo

### 1. Preservação das funções originais dos botões do volante

- Preserva a seleção nativa OEM e mantém uma cópia persistente antes de substituí-la pelo valor de captura `99`.
- A configuração nativa observada no momento prevalece sobre uma cópia antiga. Aceita valores nativos inteiros a partir de `-1`, exceto `99`; estados desconhecidos não provocam escritas presumidas.
- Relê o estado antes de capturar ou restaurar e confirma a escrita por leitura. Isso reduz conflitos, mas não torna a operação atômica contra alterações externas.
- No modo normal, captura com `99` somente quando a personalização está habilitada **e há uma ação curta personalizada**. Apenas ações dupla/longa não retiram a ação curta original.
- Preserva o comportamento do modo discreto e o mecanismo existente de reconhecimento/disparo dos cliques.
- Exibe o aviso sobre possível sobreposição: uma ação dupla personalizada ainda pode coexistir com duas ações curtas OEM. **Essa sobreposição não foi corrigida nesta versão.**

### 2. Foco global do controle de ar-condicionado

- A ponte de temas `1.0.2` lê e grava a preferência do aplicativo `lastClusterAcConfig`, compartilhada entre temas, com os valores `TEMPERATURE` ou `FAN_SPEED`. A API JavaScript usa `temp` e `fan`; não se trata de alterar uma preferência global do sistema Android.
- O tema restaura o foco ao iniciar; alternar o foco ou restaurá-lo não envia comandos para mudar temperatura ou ventilação.
- Inclui regressões para restauração, ar desligado/telemetria desconhecida e simulador de navegador.
- **Incluído no APK:** Default `1.4.44`, com ponte mínima `1.0.2`, recompilado com esse comportamento. O Default é lido do recurso do APK, sem depender de atualizar uma cópia antiga pelo número de versão.
- **Entregues separadamente:** Minimalist `1.0.4` e ApexGT `1.0.16`, também com ponte mínima `1.0.2`. Esses dois pacotes não estão embutidos no APK e exigem instalação própria. O APK não substitui silenciosamente temas personalizados restaurados.

### 3. Inicialização local e validação do Telnet/Shizuku

- Comandos Telnet serializados, com delimitadores exclusivos, validação de status de saída, identidade root, caminhos e PID, além de limites de tamanho e tempo.
- Rejeita controles e respostas malformadas, sem transformar silenciosamente conteúdo inválido em números ou marcadores aparentemente válidos. Interrupção anterior ao comando não transmite o comando.
- Reutiliza um Binder Shizuku já disponível antes de tentar iniciar outro servidor; mantém a verificação normal de autorização do Shizuku.
- Mantém um listener com identidade estável, removido corretamente no encerramento, e verifica novamente o Binder antes de executar o inicializador.
- Faz uma tentativa limitada nos endereços locais literais `::1` e `127.0.0.1`, sem varrer a rede ou descobrir endereços LAN. O UID observado não é usado como autorização nem como motivo para rejeitar um Binder válido.
- A descoberta local tem orçamento de 5 segundos, com tolerância limitada de leitura; as etapas posteriores têm limites próprios. Isso **não** significa que toda a inicialização termine em 5 segundos.
- Em caso de falha, aguarda passivamente a chegada do Binder, sem repetir indefinidamente a sondagem ou reiniciar o serviço em loop.

### 4. Sem liberação global automática do firewall

- Remove as chamadas automáticas que liberavam globalmente `INPUT`/`OUTPUT` com `ACCEPT` e a implantação do watchdog que repetia essa liberação.
- Não substitui isso por regras amplas presumidas e não adiciona uma lista de permissões de rede sem evidência.
- Essa mudança de código **não limpa regras ou processos antigos que já existam no dispositivo**. Downloads, DNS/HTTPS e o portal de envio de arquivos precisam de validação própria conforme o ambiente.

## O que foi feito na instalação, mas não é funcionalidade do APK

- A instalação observada recebeu **UID 10052**, com auxílio do procedimento de instalação/hook; esse UID permaneceu após reboot real. O APK não escolhe, reserva nem garante essa faixa de UID em outra instalação. Consulte o [procedimento documentado de instalação e UID](installation-uid-recovery-1.md).
- Preservação, migração e verificação das configurações foram realizadas por ferramentas externas. Não são um mecanismo novo de migração embutido no aplicativo.
- Diagnósticos e cópias de segurança também são externos ao APK.
- Foi utilizada uma nova identidade de assinatura, distinta da antiga test.2. Por isso, aquela troca exigiu reinstalação; atualizações futuras assinadas com a mesma nova chave podem seguir o fluxo normal de atualização, respeitadas as demais exigências do Android.

## Validação e limites conhecidos

- Na preparação do APK: **630 testes Android**, sem falhas, erros ou testes ignorados, além dos testes de foco dos temas e verificações de compilação/assinatura.
- No dispositivo, em 25/10/2026: instalação e configurações verificadas; inicialização root pelo loopback IPv6 ao abrir o aplicativo, Binder disponível e autorização Shizuku confirmados. O usuário confirmou o funcionamento no teste realizado.
- Em teste posterior no mesmo dia, houve **reboot real confirmado**: UID 10052 mantido, inicialização local root/Shizuku, Binder, serviços e tratamento de `BOOT_COMPLETED` observados. Os registros disponíveis não comprovam conclusivamente a origem do primeiro lançamento do aplicativo; não foi declarado resolvido o início automático em todos os boots. A mini-tela do ar-condicionado apareceu tardiamente, sozinha, sem intervenção do usuário; esse atraso permanece em investigação.
- O aviso de leitura do estado Wi-Fi por falta de `ACCESS_WIFI_STATE` é herdado: a permissão está ausente tanto na test.2 quanto neste APK. A chamada identificada captura a exceção e usa o estado em cache; esse aviso, isoladamente, não prova falha da inicialização inteira. Não houve correção dessa permissão nesta build.
- A sobreposição de janelas exige autorização Android própria (`SYSTEM_ALERT_WINDOW`). A declaração no manifest e a restauração de preferências não equivalem à concessão dessa autorização.
- A sobreposição entre duplo personalizado e dois curtos OEM permanece, conforme o aviso da interface.
- Um HTML externo de desenvolvimento pode ter precedência sobre o tema nesta build de depuração.

## Identidade pública da build

- Pacote: `br.com.redesurftank.havalshisuku`
- Versão: `1.0.0.89-acfocus-oemkeys-recovery.1` — código `2`
- Tipo: APK de depuração assinado com a nova chave de continuidade do projeto.
- SHA-256 do APK: `9398481a6565580e63d61162a8f7a556893c03c754b4d8101bc65dc8d28cfc95`
- SHA-256 do certificado público: `a147d928fd2fc5898cbab83ca2e2d558325d28a6e6bb526ffda68ca679355e57`

Este registro não contém chaves privadas, senhas ou configurações pessoais.
