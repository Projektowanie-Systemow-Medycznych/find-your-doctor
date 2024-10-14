prepare:
	docker-compose up -d
	bash wait.sh

run:
	./mvnw clean package -DskipTests && \
    docker build -t medicament-outlet . && \
    docker run --rm -p 7777:7777 --network=medicament-outlet_system --name app medicament-outlet

clean:
	docker ps -a | grep app && docker stop app && docker rm app || true
	docker-compose down && \
	./mvnw clean

migrate:
	./mvnw spring-boot:run -Dconsole=true -Dspring-boot.run.profiles=console-application
