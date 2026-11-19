# Concorrência no Banco de Dados — POC
 
API simples de saldo (depósito, saque e transferência) para testar, na prática, como tratar concorrência no banco de dados.
 
**Stack:** Java + Spring Boot + PostgreSQL
 
## O problema
 
O `@Transactional` sozinho não resolve: duas requisições podem ler o mesmo saldo antes de qualquer uma gravar, e um depósito se perde.
 
## Soluções
 
- **Lock otimista (`@Version`):** ninguém espera, mas quem perde a disputa recebe um **409 (Conflict)**, tratado no `GlobalExceptionHandler`.
- **Lock pessimista (`FOR UPDATE`):** as requisições entram em fila e o saldo fecha certo, mas fica mais lento sob carga. Com JPQL usa-se `@Lock`; com SQL nativo, o `FOR UPDATE` vai na própria query.
- **Update atômico:** o banco faz a conta num único comando (`saldo = saldo + valor`), sem intervalo entre ler e gravar.
- **Constraint `UNIQUE`:** o banco impede dados duplicados, mesmo com requisições simultâneas.
## Quando usar
 
| Situação | Solução |
|---|---|
| Conflito raro | Lock otimista |
| Disputa alta | Lock pessimista |
| Somar ou subtrair valores | Update atômico |
| Não pode duplicar | Constraint `UNIQUE` |
 
## Bônus: deadlock
 
Transferências cruzadas (A → B e B → A ao mesmo tempo) geram deadlock. O banco cancela uma das transações, e o `@Retryable` a refaz automaticamente.


## Testes de concorrência
 
Com a API rodando, os comandos abaixo disparam 10 requisições simultâneas (requer **PowerShell 7+**).
 
**Saques simultâneos:**
 
```powershell
1..10 | ForEach-Object -Parallel {
  Invoke-RestMethod -Method Put -Uri "http://localhost:8080/saldo/sacar/native" `
    -ContentType "application/json" `
    -Body '{"id": 1, "valor": 10.00}'
} -ThrottleLimit 10
```
 
**Transferências cruzadas (deadlock):**
 
```powershell
1..10 | ForEach-Object -Parallel {
  $body = if ($_ % 2 -eq 0) {
    '{"idOrigem": 1, "idDestino": 2, "valor": 10.00}'
  } else {
    '{"idOrigem": 2, "idDestino": 1, "valor": 10.00}'
  }
  Invoke-RestMethod -Method Put -Uri "http://localhost:8080/saldo/transferir" `
    -ContentType "application/json" -Body $body
} -ThrottleLimit 10
```
 
**Equivalente com curl (Git Bash, Linux ou Mac):**
 
```bash
for i in {1..10}; do
  curl -s -X PUT "http://localhost:8080/saldo/sacar/native" \
    -H "Content-Type: application/json" \
    -d '{"id": 1, "valor": 10.00}' &
done
wait
```

## Considerações finais
 
Esta POC foi criada para treinar e demonstrar, na prática, o meu conhecimento sobre como identificar e tratar problemas de concorrência no banco de dados.
