# Referência arquivada: instalador do Impulse oficial

Snapshot registrado em **25/10/2026**, para documentar o procedimento de instalação usado como referência. Estes arquivos são **cópias de código de terceiros**, não uma nova versão do instalador nem uma implementação embutida no APK recovery.1.

**Não foram executados para produzir este arquivo histórico.** Nenhuma instalação, injeção, alteração de firewall ou operação na central foi realizada nesta etapa de documentação.

## Arquivos e procedência

### `install.sh`

- Autor/publicador do Gist: `paulovitin`.
- Página de origem: https://gist.github.com/paulovitin/b3af38b151f42c6f0c2858e4914c2503
- Revisão do Gist: `230f0d0db7afe36f582eee330cc4c74d322c1f4d`, de `2026-06-10T19:24:44Z`.
- Fonte fixada nessa revisão: https://gist.githubusercontent.com/paulovitin/b3af38b151f42c6f0c2858e4914c2503/raw/230f0d0db7afe36f582eee330cc4c74d322c1f4d/install.sh
- Tamanho: **3.058 bytes**.
- SHA-256: `b4fa5f4ecd3f0ca2b27aa8b2349b4694e2d608d52786b71736a45e13a7842f29`.

O conteúdo original é um **bootstrap**: usa `echo -e` para gravar um segundo script em `/data/local/tmp/install.sh`, muda para esse diretório e invoca o script gravado. O script de instalação está embutido na cadeia de texto do bootstrap. A cópia aqui preserva exatamente essa forma original, inclusive escapes e quebras de linha; não é uma versão “desescapada” ou reescrita.

### `system_server.js`

- Endereço de origem usado pelo instalador: https://haval.joaoiot.com.br/system_server.js
- Cópia preservada na investigação de 25/10/2026; conteúdo conferido localmente para este arquivamento.
- Tamanho: **229.446 bytes**.
- SHA-256: `5b4ed8df6f9aa46a04dc59b45502defc9981bbae75045a756abe98ed5d6ff44b`.
- O endereço não contém versão ou commit. O hash identifica esta cópia, não assegura que o servidor continuará entregando os mesmos bytes.
- Uma tentativa adicional de download durante o arquivamento retornou HTTP 403; por isso foi utilizada a cópia já preservada e verificada, e não se afirma que houve um novo download bem-sucedido nessa tentativa.

O arquivo inclui a ponte Java do Frida empacotada/minificada. Seu trecho final intercepta `Settings.newUserIdLPwForThirdApp`, redefine `mFirstAvailableUid` para zero, delega a `newUserIdLPw` e define `PackageManagerService.APK_install_finish` como falso.

Zerar o índice de busca **não atribui UID 0/root ao aplicativo**, nem garante um número específico. Veja o [registro da instalação recovery.1](../../installation-uid-recovery-1.md).

## O que o instalador faz

O script embutido baixa o servidor e injetor Frida, o JavaScript acima e APKs de Shizuku e Impulse; ajusta permissões dos binários, inicia o servidor, injeta o JavaScript em `system_server` e instala os APKs com `pm install -r`.

O fluxo consulta `releases/latest` nos repositórios dos aplicativos. Portanto, executar o código em outra data pode obter versões diferentes.

O mecanismo chamado de “rollback” no script limita-se a encerrar processos que correspondam a `fridaserver` e remover os APKs temporários em determinadas falhas. **Não restaura automaticamente uma versão anterior ou as configurações dos aplicativos.** O script também reutiliza arquivos locais não vazios sem validar sua identidade criptográfica e anuncia a injeção iniciada após uma espera, sem uma verificação completa de seu efeito.

Estas observações descrevem o original, que foi preservado sem correções ou endurecimento de segurança.

## Dependências não arquivadas

Não foram baixados nem incluídos neste snapshot:

- `fridaserver.rar` e `fridainject.rar`, referenciados em `haval.joaoiot.com.br`;
- APK do Shizuku, obtido de https://github.com/RikkaApps/Shizuku/releases/latest;
- APK do Impulse, obtido de https://github.com/bobaoapae/haval-app-tool-multimidia/releases/latest.

Logo, **isto não é um pacote de instalação offline completo**. Endereços externos podem mudar, falhar ou exigir revisão. Os hashes deste registro foram calculados no computador de trabalho, não na central.

## Segurança e direitos

Injetar código em `system_server` altera um processo crítico do Android e pode causar instabilidade. Este material é uma referência para revisão técnica, não uma instrução para executar automaticamente o instalador ou repeti-lo durante a condução.

Os arquivos foram mantidos byte a byte, sem remover comentários ou avisos eventualmente presentes. Não foi identificada uma declaração explícita de licença nos dois arquivos capturados. Este registro não atribui a eles uma licença nova nem presume que a licença do repositório se aplique ao código de terceiros. A autoria e os direitos dos respectivos titulares permanecem preservados.

Não há chaves privadas, senhas, configurações pessoais ou dados do veículo neste snapshot.
