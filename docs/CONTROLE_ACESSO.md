# Controle de Acesso de Visitantes (RN01 a RN05)

Caso de uso testado no trabalho de QTS (P2). Este documento descreve as rotas criadas e como subir
o backend em modo de teste.

## Premissas adotadas

| Regra | Premissa |
|-------|----------|
| RN01 | "6 meses" = meses de calendário; o último dia é inclusivo (lista atualizada em 11/05 vale até 11/11). |
| RN02 | Data inicial/final e hora inicial/final são inclusivas. Sábado e domingo exigem `permiteFimSemana`; feriado exige `permiteFeriado`; sábado que também é feriado exige as duas. |
| RN05 | A saída continua permitida depois do cancelamento; só a entrada é negada. |
| Estados | O estado da autorização (AGENDADA, VIGENTE, CANCELADA, EXPIRADA) é calculado pelas datas e pelo cancelamento, não é gravado. |
| Lista | Revisão com data futura é rejeitada na API (não há regra para ela). |

## Como subir em modo de teste (banco H2, relógio ajustável)

```
./gradlew bootRun --args='--spring.profiles.active=teste'
```

No perfil `teste` o banco é H2 em memória (nunca o de produção) e existem as rotas `/admin/teste/**`
(exigem papel ADMIN). O ADMIN inicial vem de `AdminInicial` (`admin@condominio.com` / `admin123`, a menos
que `app.admin.*` esteja configurado).

| Rota | Função |
|------|--------|
| `POST /admin/teste/massa-b0` | Restaura a massa-base B0 relativa ao dia D do relógio e devolve os ids |
| `PUT /admin/teste/relogio` | Corpo `{"dataHora":"2026-12-01T10:00:00"}`; o tempo segue correndo a partir daí |
| `DELETE /admin/teste/relogio` | Volta ao relógio real |
| `GET /admin/teste/relogio` | Lê o relógio |

Usuários de teste criados pela massa B0 (senha `Teste@123`): `portaria.p01@teste.local` (PORTARIA) e
`responsavel.r01@teste.local` (MORADOR).

## Rotas do caso de uso

| Rota | Papel | Passo do fluxo |
|------|-------|----------------|
| `POST /api/acessos/avaliar` | PORTARIA, SINDICO, ADMIN | 1 a 4: consulta e avaliação (200; 503 se a consulta falhar) |
| `POST /api/acessos/entrada` | PORTARIA, SINDICO, ADMIN | 5 e 6: revalida e grava a entrada (201; 403 negada; 500 falha ao gravar; 503) |
| `PUT /api/acessos/{id}/saida` | PORTARIA, SINDICO, ADMIN | 7 e 8: grava a saída no mesmo registro (200; 404; 409; 500) |
| `GET /api/acessos`, `/abertos`, `/{id}` | PORTARIA, SINDICO, ADMIN | Consulta de registros |
| `POST /api/autorizacoes` | MORADOR, SINDICO, ADMIN | Concede autorização (RN02, RN03) |
| `PUT /api/autorizacoes/{id}/cancelar` | MORADOR, SINDICO, ADMIN | Cancela (RN05) |
| `GET /api/autorizacoes`, `/{id}` | autenticado | Lê autorizações com o estado calculado |
| `PUT /api/listas/{unidadeId}` | MORADOR, SINDICO, ADMIN | Atualiza a lista da unidade (RN01) |
| `GET /api/listas/{unidadeId}` | autenticado | Lê a lista vigente |
| `POST /api/feriados`, `DELETE /api/feriados/{id}` | SINDICO, ADMIN | Calendário de feriados (RN02) |
| `GET /api/feriados` | autenticado | Lista feriados |

Motivos de negação devolvidos em `motivo`: `CADASTRO_NAO_ENCONTRADO`, `CADASTRO_INATIVO`,
`DOCUMENTO_DIVERGENTE`, `FORA_DA_LISTA`, `LISTA_VENCIDA`, `SEM_AUTORIZACAO`, `AUTORIZACAO_OUTRA_UNIDADE`,
`AUTORIZACAO_OUTRA_PESSOA`, `AUTORIZACAO_CANCELADA`, `ANTES_DO_PERIODO`, `APOS_O_PERIODO`,
`FORA_DO_HORARIO`, `FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO`, `SERVICO_INDISPONIVEL`, `FALHA_AO_GRAVAR`.

## Testes automatizados e cobertura

```
./gradlew clean test jacocoTestReport        # Linux/macOS
.\gradlew.bat clean test jacocoTestReport    # Windows
```

- Resultado dos testes: `build/reports/tests/test/index.html`
- Cobertura (JaCoCo): `build/reports/jacoco/test/html/index.html`

Os testes usam mocks dos repositórios; `CondominioApplicationTests` sobe o contexto com H2
(`src/test/resources/application.properties`).
