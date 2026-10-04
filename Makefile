.PHONY: setup dev test demo lint

setup:
	scripts/gen-secrets.sh

dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build

test:
	cd backend && ./mvnw test
	cd frontend && npm ci && npm run lint && npm run build && npm test -- --run

demo:
	SPRING_PROFILES_ACTIVE=demo docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build

lint:
	scripts/gitleaks.sh detect --source . --no-banner

