# language: pt

Funcionalidade: Teste de buscar o usuario

  Cenário: Busca usuario com sucesso
    Dado que busco um usuario que ja esta cadastrado
    Quando busco esse usuario
    Entao recebo uma resposta que o usuario foi encontrado com sucesso

  Cenário: Busca usuario não cadastrado
    Dado que busco um usuario nao cadastrado
    Quando busco esse usuario
    Entao recebo uma resposta que o usuario nao foi encontrado
