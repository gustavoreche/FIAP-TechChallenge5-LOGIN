# language: pt

Funcionalidade: Teste de cadastro de usuario

  Cenário: Cadastra usuario com sucesso
    Dado que tenho dados validos de um usuario
    Quando cadastro esse usuario
    Entao recebo uma resposta que o usuario foi cadastrado com sucesso

  Cenário: Cadastra usuario já cadastrado
    Dado que tenho dados validos de um usuario que ja esta cadastrado
    Quando cadastro esse usuario
    Entao recebo uma resposta que o usuario ja esta cadastrado
