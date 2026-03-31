# estapar

backend para gerenciamento de estacionamento: controle de vagas, entrada/saída de veículos e cálculo de receita por setor.

## requisitos

- java 21
- docker

## como rodar

**1. suba o banco:**
```bash
docker run -d --name estapar-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=estapar -p 3306:3306 mysql:8.0
```

**2. suba o simulador:**
```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

**3. rode a aplicação:**
```bash
./gradlew bootRun
```

a aplicação sobe na porta **3003** e popula o banco automaticamente com os dados do simulador.

## variáveis de ambiente

| variável             | padrão                    | descrição                    |
|----------------------|---------------------------|------------------------------|
| db_url               | jdbc:mysql://localhost:3306/estapar | url de conexão jdbc  |
| db_username          | root                      | usuário do banco             |
| db_password          | root                      | senha do banco               |
| garage_simulator_url | http://localhost:3001      | url base do simulador        |

## api

### post /webhook

recebe eventos do simulador.

**entry**
```json
{
  "license_plate": "ZUL0001",
  "entry_time": "2025-01-01T12:00:00.000Z",
  "event_type": "ENTRY"
}
```

**parked**
```json
{
  "license_plate": "ZUL0001",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}
```

**exit**
```json
{
  "license_plate": "ZUL0001",
  "exit_time": "2025-01-01T14:30:00.000Z",
  "event_type": "EXIT"
}
```

### get /revenue

retorna a receita total de um setor em uma data.

```json
{ "date": "2025-01-01", "sector": "A" }
```

resposta:
```json
{ "amount": 150.00, "currency": "BRL", "timestamp": "2025-01-01T23:59:59Z" }
```

## testes

```bash
./gradlew test
```

cobrem: precificação dinâmica, tolerância de 30 minutos, arredondamento de horas, eventos do webhook e agregação de receita.

## regras de negócio

- primeiros 30 minutos: gratuito
- após 30 minutos: cobrado por hora cheia (arredondamento para cima), mínimo 1 hora
- preço dinâmico calculado na entrada:
  - abaixo de 25% de ocupação: -10%
  - entre 25% e 50%: sem ajuste
  - entre 50% e 75%: +10%
  - acima de 75%: +25%
- setor lotado: novas entradas bloqueadas até uma vaga ser liberada
