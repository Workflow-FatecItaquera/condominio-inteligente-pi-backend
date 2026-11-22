# Roteiro de execução dos testes de sistema (caixa-preta)

Passo a passo de cada caso de teste da planilha `Planilha_de_Teste_Lume.xlsx` (aba "Caso de Uso"),
para executar com o cliente HTTP do IntelliJ (`docs/controle-acesso.http`) e gravar um vídeo por caso.

Suba o backend: `./gradlew bootRun --args='--spring.profiles.active=teste'` (banco H2 em memória).

## Perfis e tokens

| Sigla | Usuário | Senha | Papel |
|-------|---------|-------|-------|
| ADMIN | `admin@condominio.com` | `admin123` | ADMIN |
| P01 | `portaria.p01@teste.local` | `Teste@123` | PORTARIA (só existe depois da primeira B0) |
| R01 | `responsavel.r01@teste.local` | `Teste@123` | MORADOR (só existe depois da primeira B0) |

Faça o login em `POST /auth/signin` e use o token em `Authorization: Bearer <token>`.

## Atalhos usados abaixo

| Atalho | Chamada |
|--------|---------|
| **Preparar** | ADMIN: `DELETE /admin/teste/falhas`; `PUT /admin/teste/relogio` com `{"dataHora":"2026-11-11T09:00:00","congelar":"true"}`; `POST /admin/teste/massa-b0`. Anote os ids devolvidos (V01, V02, U101, U102, A01). **A B0 sempre é restaurada com o relógio no dia D = 11/11/2026.** |
| **Preparar (rev AAAA-MM-DD)** | Igual a Preparar, mas com `POST /admin/teste/massa-b0?revisaoLista=AAAA-MM-DD` |
| **Relógio X** | ADMIN: `PUT /admin/teste/relogio` com `{"dataHora":"X","congelar":"true"}` (congelado, o instante é exato) |
| **Lista U101 = [..], data** | R01: `PUT /api/listas/{U101}` com `{"visitanteIds":[..],"atualizadaEm":"data"}` (a versão mais nova vence; em empate de data vence a última gravada) |
| **Cancelar A01** | R01: `PUT /api/autorizacoes/{A01}/cancelar` |
| **Nova autorização** | R01: `POST /api/autorizacoes` com visitanteId, unidadeId, dataInicio, dataFim, horaInicio, horaFim, permiteFimSemana, permiteFeriado |
| **Feriado data** | ADMIN: `POST /api/feriados` com `{"data":"data","nome":"Feriado de teste"}` |
| **Avaliar** | P01: `POST /api/acessos/avaliar` com `{"visitanteId":V01,"unidadeId":U101,"documento":"DOC-V01"}` (200 com `permitido` e `motivo`; 503 se a consulta falhar) |
| **Entrada** | P01: `POST /api/acessos/entrada` com `{"autorizacaoId":A01,"documento":"DOC-V01"}` (201; 403 negada; 500 falha ao gravar; 503) |
| **Saída** | P01: `PUT /api/acessos/{idDoRegistro}/saida` (200; 409 já encerrada; 500 falha ao gravar) |
| **Conferir registros** | P01: `GET /api/acessos` e `GET /api/acessos/{id}` |

Os instantes são sempre de 2026. Evidência em vídeo: mostre o relógio (`GET /admin/teste/relogio`), a chamada e a resposta.

## Casos da planilha

| CT | Passos | Resultado esperado |
|----|--------|--------------------|
| #01 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar. Entrada. Relógio 2026-11-11T11:00:00. Saída. | Avaliar permite. Entrada 201 e, na saída, o mesmo registro com `entradaEm` 10:00:00, `saidaEm` 11:00:00 e unidade U101. |
| #02 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar com `"documento":"DOC-OUTRO"`. | `permitido=false`, motivo `DOCUMENTO_DIVERGENTE`. |
| #03 | Preparar. Lista U101 = [V02], 2026-05-11 (V01 sai da lista). Relógio 2026-11-11T10:00:00. Avaliar. | `FORA_DA_LISTA`. |
| #04 | Preparar (rev 2026-05-10). Relógio 2026-11-11T10:00:00. Avaliar. | `LISTA_VENCIDA` (venceu em 10/11). |
| #05 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar. | Permite (último dia de validade da lista). |
| #06 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar com `visitanteId=V02`, `unidadeId=U101`, `documento=DOC-V02`. | `AUTORIZACAO_OUTRA_PESSOA`. |
| #07 | Preparar. Relógio 2026-11-11T08:00:00. Avaliar. | Permite (início da data e da hora). |
| #08 | Preparar. Lista U101 = [V01,V02], 2026-11-11. Relógio 2026-11-30T10:00:00. Avaliar. | Permite (data final inclusiva). |
| #09 | Preparar. Relógio 2026-11-11T18:00:01. Avaliar. | `FORA_DO_HORARIO`. |
| #10 | Preparar. Lista U101 = [V01,V02], 2026-11-11. Cancelar A01. Nova autorização V01/U101, 2026-11-11 a 2026-11-30, 08:00:00 a 18:00:00, `permiteFimSemana=true`, `permiteFeriado=false` (é a "A01 com permiteFimSemana"). Relógio 2026-11-14T10:00:00 (sábado). Avaliar. | Permite. Use o `autorizacaoId` devolvido por Avaliar. |
| #11 | Preparar. Lista U101 = [V01,V02], 2026-11-11. Feriado 2026-11-12. Relógio 2026-11-12T10:00:00. Avaliar. | `FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO`. |
| #12 | Preparar. Relógio 2026-11-11T10:00:00. Cancelar A01. Avaliar. | `AUTORIZACAO_CANCELADA`. |
| #13 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar (permite; anote `autorizacaoId`). Cancelar A01. Entrada. Conferir registros. | Entrada 403 `AUTORIZACAO_CANCELADA`; nenhum registro criado. |
| #14 | Preparar. Relógio 2026-11-11T10:00:00. Entrada (anote o id do registro). Relógio 2026-11-11T10:30:00. Cancelar A01. Relógio 2026-11-11T11:00:00. Saída. Relógio 2026-11-11T11:05:00. Entrada. Conferir registros. | Saída 200 (RN05 não impede a saída); nova entrada 403 `AUTORIZACAO_CANCELADA`; histórico preservado. |
| #15 | Preparar. Relógio 2026-11-11T10:00:00. `PUT /admin/teste/falhas/GRAVAR_ENTRADA`. Entrada. Conferir registros. Desligar falhas. | 500 `FALHA_AO_GRAVAR`; nenhum registro criado. |
| #16 | Preparar. Relógio 2026-11-11T10:00:00. Entrada. `PUT /admin/teste/falhas/GRAVAR_SAIDA`. Relógio 2026-11-11T11:00:00. Saída. `GET /api/acessos/{id}`. Desligar falhas. | Saída 500; `saidaEm` continua nulo (visita em aberto). |
| #17 | Preparar. Relógio 2026-11-11T10:00:00. `PUT /admin/teste/falhas/CONSULTA`. Avaliar. Conferir registros. Desligar falhas. | 503 `SERVICO_INDISPONIVEL`; nenhuma liberação. |
| #18 | Preparar (rev 2026-05-10). Relógio 2026-11-11T10:00:00. Avaliar. Lista U101 = [V01,V02], 2026-11-11. Avaliar de novo. `GET /api/autorizacoes/{A01}`. | 1ª tentativa `LISTA_VENCIDA`; 2ª permite; A01 continua `VIGENTE`. |
| #19 | Preparar. Lista U102 = [V02], 2026-11-11 (use `PUT /api/listas/{U102}`). Nova autorização V02/U102, 2026-11-18 a 2026-11-20, 08:00:00 a 18:00:00 (A02): estado `AGENDADA`. Cancelar A02: estado `CANCELADA`. Relógio 2026-11-18T10:00:00. Avaliar com `visitanteId=V02`, `unidadeId=U102`, `documento=DOC-V02`. | `AUTORIZACAO_CANCELADA`; A02 permanece cancelada. |
| #20 | Preparar. Lista U102 = [V02], 2026-11-11. Nova autorização A02 igual à do #19 (`AGENDADA`). Relógio 2026-11-18T10:00:00. Avaliar (V02/U102): permite, A02 `VIGENTE`. Relógio 2026-11-23T10:00:00. Avaliar. `GET /api/autorizacoes/{A02}`. | Permite em 18/11; em 23/11 `APOS_O_PERIODO` e A02 `EXPIRADA`. |
| #21 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar sem `visitanteId`, com `"documento":"DOC-INEXISTENTE"`. | `CADASTRO_NAO_ENCONTRADO`. |
| #22 | Preparar. `DELETE /api/visitantes/{V01}` (ADMIN). Relógio 2026-11-11T10:00:00. Avaliar. | `CADASTRO_INATIVO`. |
| #23 | Preparar. Relógio 2026-11-11T10:00:00. Avaliar com `visitanteId=V02`, `unidadeId=U102`, `documento=DOC-V02` (nenhuma autorização existe para V02 nem para U102). | `SEM_AUTORIZACAO`. |
| #24 | Preparar. Relógio 2026-11-10T10:00:00 (terça). Avaliar. | `ANTES_DO_PERIODO` (A01 começa em 11/11). |
| #25 | Preparar. Lista U101 = [V01,V02], 2026-11-11. Relógio 2026-12-01T10:00:00. Avaliar. | `APOS_O_PERIODO` (A01 termina em 30/11). |
| #26 | Preparar. Relógio 2026-11-11T07:59:59. Avaliar. | `FORA_DO_HORARIO`. |
| #27 | Preparar. Relógio 2026-11-11T18:00:00. Avaliar. | Permite (horário final inclusivo). |
| #28 | Preparar. Lista U101 = [V01,V02], 2026-11-11. Relógio 2026-11-14T10:00:00 (sábado). Avaliar. | `FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO`. |
| #29 | Preparar. Lista U101 = [V01,V02], 2026-11-11. Feriado 2026-11-12. Cancelar A01. Nova autorização V01/U101, 2026-11-11 a 2026-11-30, 08:00:00 a 18:00:00, `permiteFeriado=true`, `permiteFimSemana=false`. Relógio 2026-11-12T10:00:00. Avaliar. | Permite (feriado com autorização expressa). |

## Observações

- O id da A01 muda a cada restauração da B0; use sempre os ids devolvidos pela última chamada.
- `atualizadaEm` das listas não pode ser futura em relação ao relógio real do computador (validação do Bean Validation); as datas da planilha já são passadas.
- Depois de cada caso, `DELETE /admin/teste/relogio` volta ao relógio real, se necessário.
- Registre na planilha o status (OK/NOK), a descrição da evidência, o tipo de defeito e o link do vídeo.
