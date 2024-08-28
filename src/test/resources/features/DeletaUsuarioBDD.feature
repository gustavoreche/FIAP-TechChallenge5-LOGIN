# language: pt

Funcionalidade: Teste de deletar o usuario

  Cenário: Deleta usuario com sucesso
    Dado que informo um usuario que ja esta cadastrado
    Quando deleto esse usuario
    Entao recebo uma resposta que o usuario foi deletado com sucesso

  Cenário: Deleta usuario não cadastrado
    Dado que informo um usuario nao cadastrado
    Quando deleto esse usuario
    Entao recebo uma resposta que o usuario nao foi cadastrado
