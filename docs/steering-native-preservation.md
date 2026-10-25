# Preservação do padrão da multimídia nos botões do volante

## Escopo

Cada botão mantém sua escolha nativa quando o **toque curto** está em
**Padrão da multimídia**, inclusive se houver uma ação personalizada no duplo
ou no longo. O padrão é a função escolhida pelo usuário no carro, não uma função
fixa do Impulse. A correção não recria nem reenvia comandos nativos.

## Combinação não exclusiva

Nessa configuração, o duplo pode executar **duas ações curtas nativas**, além da
ação do Impulse. O longo pode abrir o menu original, além da ação personalizada.
Um aviso por botão aparece nas configurações enquanto essa combinação estiver
ativa. Ao voltar todos os gestos ao padrão ou desativar a personalização, o
aviso desaparece. Não há promessa de exclusividade entre OEM e Impulse.

## Verificação visual

Para cada botão, ativar a personalização e verificar:

- Curto padrão + duplo personalizado: aviso específico do duplo.
- Curto padrão + longo personalizado: aviso específico do longo.
- Curto padrão + ambos personalizados: aviso com as duas limitações.
- Todos no padrão, curto personalizado ou recurso desligado: nenhum aviso de
  preservação dessa combinação. O rótulo já existente do longo continua avisando
  que a configuração do carro abre junto.

A seleção nativa deve ser conferida após reinício e reconfiguração. Testes de
hardware são feitos com o carro parado; o aviso não comprova compatibilidade
com todos os firmwares.

## Preservação e falhas

A configuração nativa atual prevalece sobre um backup antigo. Antes de capturar o
botão com o valor 99, o backup deve ser persistido com sucesso; uma segunda leitura
impede sobrescrever uma mudança já observada. A restauração exige valor atual 99 e
backup válido. Valores desconhecidos não causam gravação. Cada gravação de configuração
é confirmada por leitura. A API Settings não oferece compare-and-set: uma gravação
externa ainda pode ocorrer entre a última leitura e a gravação.

Os testes de política cobrem escolhas -1, 0, 98 e acima de 99, backups ausentes ou
obsoletos, falhas de persistência, mudanças entre leituras e confirmação de gravação.
Os testes de contrato verificam o adaptador e a ligação dos avisos; não substituem
validação visual ou testes de hardware.
