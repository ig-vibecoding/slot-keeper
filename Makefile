.PHONY: setup dev test demo lint

setup:
	bash scripts/gen-secrets.sh

dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build

test:
	cd backend && ./mvnw -q test
	cd frontend && npm ci && npm run lint && npm test -- --run && npm run build

demo:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build

lint:
	gitleaks detect --source . --no-banner --redact
