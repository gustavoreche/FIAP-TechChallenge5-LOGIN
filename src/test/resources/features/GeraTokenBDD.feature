# language: pt

Funcionalidade: Teste de gerar token

  Cenário: Gera token com sucesso
    Dado que informo dados validos de um usuario
    Quando gero o token desse usuario
    Entao recebo uma resposta que o token foi gerado com sucesso

  Cenário: Gera token para usuário não cadastrado
    Dado que informo dados invalidos de um usuario
    Quando gero o token desse usuario
    Entao recebo uma resposta que o token não foi gerado
