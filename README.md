# Projeto Integrador Final: Condomínio Inteligente

**Grupo:** Workflow<br>
**Repositório (Front):** [condominio-inteligente-pi-frontend](https://github.com/Workflow-FatecItaquera/condominio-inteligente-pi-frontend) <br>
**Repositório (Back):** [condominio-inteligente-pi-frontend](https://github.com/Workflow-FatecItaquera/condominio-inteligente-pi-backend)

**API REST** para gestão de condomínios, usuários, apartamentos, visitantes, reservas de áreas comuns, encomendas, ocorrências, cobranças, pagamentos e despesas. A aplicação centraliza informações administrativas e financeiras do condomínio, facilitando o acesso dos moradores e o trabalho da equipe responsável pela gestão.

<br>

## **Tecnologias:** 

##### Linguagem
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)

##### Framework e ecossistema
![SpringBoot](https://img.shields.io/badge/Spring_Boot_4-6DBF65?style=for-the-badge&logo=SpringBoot&logoColor=white)
![SpringData](https://img.shields.io/badge/Spring_Data_JPA-58A851?style=for-the-badge&logo=SpringBoot&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/Spring_Security_%26_JWT-3d7a37?style=for-the-badge&logo=SpringSecurity&logoColor=white)

##### Banco de dados
![MySQL](https://img.shields.io/badge/MySQL-005D87?style=for-the-badge&logo=MySQL&logoColor=white)

##### Build e hospedagem
![Gradle](https://img.shields.io/badge/Gradle-002E38?style=for-the-badge&logo=Gradle&logoColor=white)
![AlwaysData](https://img.shields.io/badge/AlwaysData-E0538E?style=for-the-badge&logo=AlwaysData&logoColor=white)

<br>

## Arquitetura do projeto

### Backend

O backend utiliza uma arquitetura em camadas, organizada dentro de `src/main/kotlin/com/example/condominio`:

- **`model`** — entidades persistidas no banco de dados por meio do JPA.
- **`repository`** — interfaces de acesso aos dados utilizando Spring Data JPA.
- **`service`** — regras de negócio e operações de CRUD.
- **`dto`** — objetos utilizados na entrada e na saída de dados da API.
- **`mapper`** — conversão entre entidades e DTOs.
- **`controller`** — endpoints REST e tratamento das requisições HTTP.
- **`security`** — autenticação, geração e validação de JWT e regras de autorização.
- **`src/main/resources`** — configurações da aplicação e do banco de dados.
- **`src/test`** — testes automatizados e configurações exclusivas do ambiente de teste.

### Frontend

O frontend é desenvolvido em um repositório separado, utilizando Kotlin Multiplatform e Compose Multiplatform para Android, Desktop e Web. A criação inicial do repositório ficou sob responsabilidade de Lucas Retanero.

<br>

## Instalação e execução

### Pré-requisitos:
- Ter uma cópia do repositório https://github.com/Workflow-FatecItaquera/condominio-inteligente-pi-backend.git

```sh
git clone https://github.com/Workflow-FatecItaquera/condominio-inteligente-pi-backend.git
```

Além disso:

- Ter o **JDK 17** instalado (conferir com `java -version` em CMD). Outras versões fazem o Gradle falhar na hora de achar o Java.
- Ter o **MySQL** instalado e rodando na porta 3306. Não é necessário criar banco, ele é criado automaticamente na primeira execução do projeto.

> Recomendável: Utilizar **Postman** (ou similar) para testar as rotas.

<br>

### Como executar:

1. Renomeie `src\main\resources\properties.example` para `application.properties` e preencha as propriedades com as configurações locais do banco de dados e da aplicação.

> O `application.properties` tem senhas e **não deve ser enviado ao GitHub**. Só o `properties.example` vai para o repositório.

2. Abra o CMD na raiz da pasta clonada e execute o comando:

```sh
.\gradlew.bat bootRun
```

3. Após a inicialização, a API poderá ser acessada em `http://localhost:8080`.

Para executar os testes automatizados, utilize:

```sh
.\gradlew.bat test
```

<br>

## Como contribuir

### Como desenvolver:

1. Após abrir o repositório, crie uma nova branch a partir da `main`. É nessa branch que você irá trabalhar na feature a ser desenvolvida.

- PADRÃO: NOME/o-que-fez.
- Ex: KEVYN/dashboard

utilize o código:
```sh
git checkout -b SUA/branch
```

<br>

2. Após terminar a implementação dentro de sua branch, verifique antes se não há mudanças posteriores na branch `main`:

```sh
git checkout main
git pull origin main
git checkout SUA/branch
git merge main
```

<br>

3. Faça o commit da sua branch, depois envie-a para o repositório remoto no github.
```sh
git add .
git commit -m "Comentário identificador"
git push SUA/branch
```

<br>

4. Quando seu trabalho estiver completamente finalizado e você for trabalhar em outra feature, solicite o pull request da sua branch.

> Após a avaliação e aprovação da sua branch, o conteúdo dela será incluido na branch "develop" e a branch será excluída.

<br>

### Como Testar:

Todas as rotas `/api/...` exigem um token JWT. Apenas as rotas `/auth/**` são abertas.

1. **Login com o admin inicial** 

Use o e-mail e a senha do administrador inicial configurado na aplicação:

- `POST http://localhost:8080/auth/signin`

```json
{
  "email": "admin@condominio.com",
  "senha": "SUA_SENHA_DE_ADMIN"
}
```

A resposta é o token JWT.

<br>

2. **Usar o token:** 

Em cada requisição, abra **Authorization** > **Bearer Token** e cole o token.<br> No Postman, o mais prático é colocar o token uma vez na **Collection** e deixar as requisições em *Inherit auth from parent*.

<br>

3. **Criar um usuário novo:** 

(sempre registrado como *MORADOR*):

- `POST http://localhost:8080/auth/signup`

```json
{
  "nome": "Maria Silva",
  "cpf": "12345678901",
  "email": "maria@teste.com",
  "senha": "123456",
  "telefone": "11999998888"
}
```

<br>

4. **Mudar o papel de um usuário** (somente ADMINS):

- `PUT http://localhost:8080/api/usuarios/2/papeis`

```json
{
  "papel": "SINDICO"
}
```

> O usuário precisa fazer login de novo para receber um token com o papel novo.

<br>

> Tipos de usuário <br><br>
**`MORADOR`** | Reserva áreas comuns, cadastra visitantes, abre ocorrências e consulta cobranças.<br><br>
**`PORTARIA`** | Registra encomendas e visitantes e acompanha ocorrências.
<br><br>
**`SÍNDICO`** | Gerencia áreas comuns, reservas, ocorrências e finanças.
<br><br>
**`ADMIN`** | Acesso total ao gerenciamento de condomínios, usuários e permissões.

<br>


### Principais rotas

A API disponibiliza endpoints REST para gerenciamento dos recursos do condomínio, seguindo o padrão CRUD:

- `GET /api/{recurso}` — Listagem
- `GET /api/{recurso}/{id}` — Consulta por ID
- `POST /api/{recurso}` — Cadastro
- `PUT /api/{recurso}/{id}` — Atualização
- `DELETE /api/{recurso}/{id}` — Exclusão

**Condomínios e usuários**
- `/api/usuarios`
- `/api/condominios`
- `/api/apartamentos`

**Gestão condominial**
- `/api/areascomuns`
- `/api/reservas`
- `/api/visitantes`
- `/api/encomendas`
- `/api/ocorrencias`

**Gestão financeira**
- `/api/cobrancas`
- `/api/pagamentos`
- `/api/despesas`

**Autorização**
- `/auth/signin`
- `/auth/signup`

> Alguns recursos possuem dependências por chave estrangeira. Recomenda-se cadastrar primeiro o condomínio, seguido dos apartamentos e áreas comuns. Depois, podem ser cadastradas reservas, encomendas e cobranças e, por último, os pagamentos.

<br>

## Preparação para a entrega

Antes de compactar o backend, execute:

```sh
.\gradlew.bat clean
```

Depois, confirme que a pasta `build` foi removida e que ela não está presente no arquivo `.zip`. O arquivo do backend deve ser nomeado como `Condominio-Inteligente-backend.zip`.

No projeto frontend, também é necessário executar o `gradlew clean` e remover tanto a pasta `build` da raiz quanto a pasta `shared/build` antes de criar o arquivo `Condominio-Inteligente-frontend.zip`.

<br>

## FAQ

- Erro: `Cannot find a Java installation ... languageVersion=17`: <br> • Verificar a instalação do JDK 17

- Erro: `Could not resolve placeholder 'app.jwt.secret'`: <br> • Verificar a 
existência do `application.properties` e se possui as chaves `app.jwt.*`

- Erro: `Connection refused`: <br> • Verificar se o MySQL está rodando

- Erro: `ERROR 403` nas rotas API: <br> • Verificar se o token está sendo utilizado, se expirou ou se o papel do usuário não tem permissão.

<br>

## Equipe de desenvolvimento

Projeto desenvolvido pelo grupo **Workflow**:

- **Kevyn Cavalcanti** — [@Kevyn-Cavalcanti](https://github.com/Kevyn-Cavalcanti) — Usuário, Condomínio e Apartamento.
- **Trinitydevs** — [@trinitydevs](https://github.com/trinitydevs) — Visitante, Reserva e Área Comum.
- **Pedro Fink** — [@finkDev](https://github.com/finkDev) — Pagamento Registrado, Cobrança Condominial e Despesa.
- **Lucas Retanero** — [@Lucas-Retanero](https://github.com/Lucas-Retanero) — Ocorrência, Encomenda e criação inicial do repositório frontend.
