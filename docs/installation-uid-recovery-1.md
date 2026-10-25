# Instalação da recovery.1 com UID na faixa OEM

## Escopo e resultado observado

Registro do procedimento executado em **25/10/2026** em uma central Haval H6 com Android 9, acesso root por Telnet e usuário Android 0.

- Pacote: `br.com.redesurftank.havalshisuku`.
- APK instalado: `1.0.0.89-acfocus-oemkeys-recovery.1`, `versionCode=2`.
- UID atribuído pelo Android: **10052**, dentro da faixa considerada compatível nessa central (UID de aplicativo abaixo de 11000).
- **O UID 10052 permaneceu após um reboot real**, distinguido de simples suspensão pela mudança do identificador de boot.
- O APK não escolhe nem reserva esse UID. A intervenção descrita foi externa e temporária, durante a instalação.

Este é um registro histórico, não um instalador universal nem uma recomendação para repetir o procedimento indiscriminadamente em outros firmwares.

## Por que houve reinstalação

A versão anterior `test.2` e a `recovery.1` tinham certificados de assinatura diferentes. Como não havia a chave privada da versão anterior, não era possível tratá-las como uma atualização normal com preservação automática de dados.

Antes da remoção da versão anterior, foram preservados e verificados os dados necessários e o APK de retorno. A troca só prosseguiu depois da validação do backup. A recuperação das configurações foi feita por ferramenta externa, não por uma nova função interna do Impulse.

## Intervenção no alocador de UID

Foi utilizada uma adaptação **restrita à instalação** do método baseado em Frida fornecido como referência no procedimento de instalação do projeto:

https://gist.github.com/paulovitin/b3af38b151f42c6f0c2858e4914c2503

O hook atuou temporariamente no processo Android `system_server`:

1. Interceptou `com.android.server.pm.Settings.newUserIdLPwForThirdApp`.
2. Reinicializou `mFirstAvailableUid` para `0`.
3. Delegou a escolha do identificador ao alocador padrão `newUserIdLPw`.

**Zerar esse campo não significa atribuir UID 0/root ao aplicativo.** Trata-se do índice interno de busca por uma posição livre. O alocador passou a procurar desde as posições iniciais disponíveis, em vez de continuar a busca a partir de uma posição mais alta.

O valor final depende do estado dos identificadores ocupados e da implementação do firmware. O procedimento **não garante 10052 nem garante sucesso em qualquer central**. Nesta instalação, o valor efetivamente observado foi 10052.

A adaptação também ajustou o campo OEM `PackageManagerService.APK_install_finish` para `false`, conforme o mecanismo de instalação utilizado. Esse ajuste é separado da reinicialização do índice de UID e não deve ser apresentado como uma API Android pública ou portável.

Foi empregada uma cópia limitada ao hook de instalação, sem os hooks adicionais de assinatura de outros aplicativos existentes no script mais amplo encontrado no dispositivo. O script amplo não foi executado nessa etapa.

## Sequência executada

1. Confirmar identidade do pacote/versão, estado instalado e contexto do usuário Android.
2. Desativar e parar somente o Impulse para obter cópias consistentes.
3. Preservar o APK anterior e validar o backup das configurações antes de desinstalar.
4. Ativar o hook temporário de instalação, com autorização específica para alterar o instalador em execução.
5. Desinstalar a versão anterior e instalar a recovery.1.
6. Manter o aplicativo parado durante a restauração; consultar o UID realmente atribuído, em vez de presumir um número.
7. Restaurar e verificar os dados, ajustando propriedade/permissões para o UID atribuído à nova instalação e respeitando os diretórios criados pelo Android.
8. Reativar o aplicativo e verificar funcionamento, Shizuku e configurações.
9. Encerrar os processos auxiliares de Frida usados nessa operação.
10. Após o reboot real, conferir novamente a identidade instalada e o UID: **10052 mantido**.

Não foi necessário editar manualmente o banco de pacotes do Android nem atribuir diretamente um UID escolhido. Nenhuma regra global `ACCEPT` foi aplicada ao firewall da central nessa sessão. Isso não equivale a afirmar que regras de sessões anteriores foram removidas.

## Limites e cuidados

- Um hook em `system_server` modifica um componente crítico em execução e pode causar instabilidade ou reinício da central. Não deve ser aplicado durante a condução.
- A intervenção no alocador é no instalador em execução, não uma propriedade exclusiva do APK. Não instalar outros aplicativos simultaneamente durante uma operação desse tipo.
- Encerrar os processos auxiliares não é prova, por si só, de que todos os campos Java alterados foram restaurados imediatamente. Houve depois um reboot real, e o UID instalado permaneceu.
- Reinstalar sem backup validado pode perder configurações. Não repetir desinstalações para “sortear” UIDs.
- Não usar o texto “Instalado corretamente: Não” da recovery.1 isoladamente para concluir que o UID está errado: foi identificada uma leitura de estado antigo em cache nessa tela. O UID consultado no gerenciador de pacotes é a evidência relevante.
- UID compatível, autorização Shizuku, inicialização do serviço e renderização no cluster são verificações diferentes.

## Atualizações futuras

Uma atualização normal que mantenha **o mesmo nome de pacote e a mesma chave de assinatura**, atendendo às demais condições do Android, normalmente preserva o UID e os dados. Portanto, não se deve repetir a desinstalação nem o hook apenas para atualizar uma build assinada com a chave de continuidade da recovery.1.

Uma nova instalação após desinstalação completa volta a depender do alocador. A preservação da chave de assinatura é essencial para evitar esse cenário desnecessariamente.

## Evidência pós-reboot e pendências

No novo boot foram observados inicialização local root, servidor Shizuku, chegada do Binder, inicialização dos serviços e tratamento de `BOOT_COMPLETED`. Entretanto, os registros disponíveis não demonstram conclusivamente qual evento iniciou o primeiro processo do Impulse. Assim, **não foi declarado resolvido o início automático em todos os boots**.

A mini-tela de ar-condicionado não apareceu imediatamente e retornou depois, sozinha, sem intervenção do usuário. Isso permanece como comportamento a investigar e não invalida a confirmação de persistência do UID.

## Registro documental

Esta alteração publica somente documentação e atualiza o [registro geral da recovery.1](recovery-1-changes.md). Não executa hooks, comandos na central, instalação, alterações de firewall ou testes novos. Não abre issue ou PR no upstream.

A documentação não inclui chaves privadas, senhas, configurações pessoais nem um script executável de injeção.
