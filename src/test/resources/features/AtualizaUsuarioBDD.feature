# language: pt

Funcionalidade: Teste de atualização de usuario

  Cenário: Atualiza usuario com sucesso
    Dado que tenho os dados validos de um usuario que ja esta cadastrado
    Quando atualizo esse usuario
    Entao recebo uma resposta que o usuario foi atualizado com sucesso

  Cenário: Atualiza usuario não cadastrado
    Dado que tenho os dados validos de um usuario
    Quando atualizo esse usuario
    Entao recebo uma resposta que o usuario nao esta cadastrado
