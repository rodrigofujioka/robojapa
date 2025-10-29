# RoboJAPA - Projeto de Exemplo para o Curso de Robocode

Este projeto contém exemplos de robôs para o Robocode, desenvolvidos para auxiliar os alunos do curso a entenderem os conceitos básicos e avançados de programação de robôs de batalha.

## 🚀 Robôs Incluídos

1. **RoboNinja** - Focado em esquiva e ataques precisos
2. **RoboSpartano** - Combate corpo a corpo agressivo
3. **RoboNerdao** - Estratégias avançadas de previsão de movimento

## 📋 Pré-requisitos

- Java JDK 17 ou superior
- Maven 3.6 ou superior
- Robocode 1.10.0

## 🛠️ Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/rodrigofujioka/robojapa.git
   cd robojapa
   ```

2. Compile o projeto:
   ```bash
   mvn clean install
   ```

3. Os robôs serão compilados para `C:\robocode\robots`

4. Inicie o Robocode e adicione os robôs à batalha

## 📚 Como Evoluir seu Robô

### 1. Entendendo o Básico
- Estude a classe `RoboNinja` para entender os conceitos fundamentais
- Experimente modificar os padrões de movimento
- Ajuste a potência dos tiros baseado na distância do inimigo

### 2. Técnicas Intermediárias
- Analise o `RoboSpartano` para aprender sobre:
  - Perseguição de alvos
  - Esquiva de tiros
  - Gerenciamento de energia

### 3. Estratégias Avançadas
- Explore o `RoboNerdao` para entender:
  - Previsão de movimento
  - Cálculos balísticos
  - Adaptação ao estilo do oponente

## 💡 Ideias para Melhorias

1. **Sistema de Memória**
   - Lembre-se das posições dos inimigos
   - Acompanhe os padrões de movimento dos oponentes

2. **Estratégias de Tiro**
   - Implemente diferentes padrões de tiro
   - Ajuste a potência do tiro baseado na distância e ângulo

3. **Movimento Inteligente**
   - Padrões de movimento mais imprevisíveis
   - Esquiva baseada em previsão de tiro

4. **Trabalho em Equipe**
   - Desenvolva comunicação entre robôs aliados
   - Crie estratégias de ataque coordenado

## 📖 Recursos de Aprendizado

### Documentação Oficial
- [Robocode Wiki](http://robowiki.net/wiki/Main_Page)
- [Robocode/Java API](http://robocode.sourceforge.net/docs/robocode/)

### Tutoriais Recomendados
- [Getting Started with Robocode](http://robowiki.net/wiki/Robocode/Getting_Started)
- [Robocode Movement](http://robowiki.net/wiki/Robocode/Movement)
- [Robocode Targeting](http://robowiki.net/wiki/Robocode/Targeting)

### Vídeos
- [Robocode Tutorial Series - YouTube](https://www.youtube.com/playlist?list=PLB19B6B1F3E7A3D5A)

## 🤝 Como Contribuir

1. Faça um Fork do projeto
2. Crie uma Branch para sua Feature (`git checkout -b feature/AmazingFeature`)
3. Adicione suas mudanças (`git add .`)
4. Comite suas mudanças (`git commit -m 'Add some AmazingFeature'`)
5. Faça o Push da Branch (`git push origin feature/AmazingFeature`)
6. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## ✨ Agradecimentos

- A todos os alunos e professores envolvidos no curso
- A comunidade Robocode por desenvolver essa ferramenta incrível
- Aos contribuidores que ajudaram a melhorar este projeto
