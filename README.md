curl -i -X POST localhost:8080/paragraph --data-binary "@letter-counter-data"

curl -i -X POST localhost:8080/paragraph -d "aaaabbb"

curl -i -X GET localhost:8080/letter/e

curl -i -X GET localhost:8080/paragraph/total_number
