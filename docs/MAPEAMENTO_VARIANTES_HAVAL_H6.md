# Mapeamento de variantes do Haval H6

Atualizado em: 2026-10-31.

## Escopo e limites

Registro histórico do mapeamento reunido no projeto, com o grau de confiança de cada associação. **Não é uma tabela oficial nem uma descrição completa da nomenclatura da GWM.** As associações não comprovam, por si só, ano-modelo, mercado, equipamentos ou especificações técnicas.

A confiança abaixo se refere ao histórico do projeto, não a uma certificação da fabricante:

- **Alta:** associação consolidada no histórico; no caso do PHEV19, também confirmada diretamente pelo usuário.
- **Moderada:** associação histórica a uma família comercial, com detalhes ainda a confirmar.

## Códigos e variantes

| Código interno | Variante / família comercial | Confiança | Observações e limites |
| --- | --- | --- | --- |
| `CC6470AH05NPHEV` | **PHEV19** | Alta | Associação confirmada diretamente em 26/10/2026 e reafirmada em 30/10/2026. Não deve ser agrupada como PHEV34/PHEV35. |
| `CC6470AH06DHEV` | **HEV** | Alta | Associação consolidada no histórico do projeto; não determina, isoladamente, ano-modelo ou acabamento. |
| `CC6470AH06JHEV` | **HEV** | Moderada | Subversão e ano-modelo: **a confirmar**. |
| `CC6470AH25DPHEV` | **PHEV34 / PHEV35** | Moderada | Associação à família; distinção entre D e M: **a confirmar**. |
| `CC6470AH25MPHEV` | **PHEV34 / PHEV35** | Moderada | Associação à família; distinção entre D e M: **a confirmar**. |
| `CC6470BK23CPHEV` | **GT** | Moderada | Associação histórica à família GT; detalhes de subversão e ano: **a confirmar**. |
| `CC6470BK23EPHEV` | **GT** | Moderada | Associação histórica à família GT; detalhes de subversão e ano: **a confirmar**. |

## Comparação e nomenclatura PHEV34 / PHEV35

A confirmação de `CC6470AH05NPHEV` como **PHEV19** mantém essa variante separada dos códigos `CC6470AH25DPHEV` e `CC6470AH25MPHEV`, historicamente associados à família PHEV34/PHEV35.

A mudança de denominação comercial de PHEV34 para PHEV35 foi relatada pelo usuário. Esse relato **não comprova** uma correspondência individual entre cada denominação e os códigos D/M.

Portanto:

- **Não atribuir D = PHEV34 e M = PHEV35.**
- Não inferir o significado das letras D, M, J, C, E ou N sem evidência específica.
- Não deduzir bateria, ano-modelo ou equipamentos apenas pela estrutura do código.
- Manter PHEV34/PHEV35 agrupados até existir evidência suficiente para distingui-los.

## Convenção de tanque usada no projeto — informação separada

| Família / situação | Valor de referência do projeto |
| --- | --- |
| HEV | 60 L |
| PHEV / GT | 55 L |
| Variante não identificada (fallback) | 55 L |

Esses valores registram uma **convenção de cálculo do projeto**. Não constituem comprovação da capacidade oficial de cada modelo, nem evidência para associar um código interno a uma variante. O fallback de 55 L não deve ser interpretado como identificação de um veículo desconhecido.

## Pendências e manutenção

- Confirmar a subversão e o ano correspondentes a `CC6470AH06JHEV`.
- Confirmar a diferença entre `CC6470AH25DPHEV` e `CC6470AH25MPHEV`.
- Confirmar as diferenças entre os códigos GT C/E.
- Registrar futuras evidências com fonte, data e escopo (mercado, ano-modelo e variante), sem publicar identificadores de veículos ou dados pessoais.
- Revisar a confiança apenas quando houver nova evidência; preservar explicitamente o que continuar **a confirmar**.

## Registro desta atualização

Alteração exclusivamente documental: criação deste arquivo, sem modificar código, regras de detecção, cálculos, interfaces ou comportamento do aplicativo. A revisão textual conferiu os sete códigos, os níveis de confiança e a separação entre mapeamento e convenção de tanque. Build e testes de software não foram executados, pois não houve alteração de software.
