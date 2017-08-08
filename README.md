# Build from source

```bash
git clone https://github.com/renfeng/elo-rating
cd elo-rating/cli
mvn && unzip -o target/elo-rating-cli-0.0.1-SNAPSHOT-dist.zip -d target
cd target/elo-rating-cli-0.0.1-SNAPSHOT
```

# Ranking

```bash
./elo names matches show_ranking
```

# Player stats

N.B. replace USER_NAME with a valid player name

```bash
./elo names matches USER_NAME
```

# Next matches

```bash
./elo names matches
```
