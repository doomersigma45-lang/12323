# Stalker Overhaul (Forge 1.20.1)

Recria para 1.20.1 as criaturas **Unfortunate Development** e **Cave Dweller** (modelos, animações, texturas e sons dos mods
originais, ambos MIT; veja NOTICE.md), mais fortes e com comportamento de stalker, e deixa os zumbis mais fortes que um warden.

Não precisa dos mods originais. Precisa de **Forge 1.20.1 (47.x)** e **GeckoLib 4.2+ para 1.20.1**.

## Compilar
Precisa de JDK 17 e internet.

    ./gradlew build        # Linux/Mac
    gradlew.bat build      # Windows

O .jar sai em `build/libs/`. Coloque na pasta `mods` junto com o GeckoLib.
Ou suba a pasta para um repositório no GitHub: a aba **Actions** compila e deixa o .jar em *Artifacts*.

## Configuração
`config/stalker_overhaul-common.toml` (criado no primeiro start): vida, dano, laser, stalker, spawn e quebra de blocos.
Ovos de spawn: aba *Spawn Eggs* do criativo.
