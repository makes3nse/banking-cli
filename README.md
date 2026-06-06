# Banking CLI (Non HTTP)

A semi-realistic banking command-line interface built to:
1. Test and validate a custom Dependency Injection container implementation.
2. Design and fully understand DDD architecture.
3. Solve a real-world problem
4. Implement everything by hand with almost no copy/paste.
5. Learn and write tests

## Overview

This project simulates a banking system with a CLI interface, following clean architecture principles. The primary goal is to design and fully understand Value Objects, design better and clean Entities, and get a better vision on all hidden architectural concepts hidden under frameworks in Spring Boot, to improve general Java knowledge. Also, the goal is to verify DI container functionality—service resolution, dependency injection, and lifecycle management—in a non-trivial, real-world context.

## Features

- Customer registration interactions (login, sign up, profile mgmt)
- Session Token
- Refresh Token authentication 
- JWT implementation
- Account management (create, check balance)
- Transactions (deposit, withdraw, transfer)
- Transaction history viewing

## CLI Commands

1. Register customer
2. Login (receives auth token)
3. Create an account
4. Check balance
5. Deposit money
6. Withdraw money
7. Transfer between accounts
8. View transaction history

## Architecture
CLI → Service → Repository → Domain

**Example flow:** `deposit 100` → CommandHandler → TransactionService → AccountRepository → Domain model update → CLI output

## Key Design Decisions

- **Storage**: In-memory (`Map<ID, Entity>`) + JSON locally [Future upgrade: PostgreSQL]
- **IDs**: Digit and ASCII identifiers / UUID
- **Tokens**: Session Token, Refresh token, JWT, Idempotency Key
- **Money**: `BigDecimal` >> Multiple currencies
- **Validation**: No negative transfers, currency safety, sufficient balance checks, valid token enforcement, operation idempotency validation, privileges

## Project Structure

- `cli/` - User interface layer (commands, menu, input parsing)
- `application/` - Service interfaces, implementations, DTOs, mappers
- `domain/` - Core business models, value objects, exceptions
- `common/` - Layer-wide shared modules of general purpose (Rules, Generators)
- `infrastructure/` - Repositories, security, utilities

## Purpose

Controlled environment to validate DI container behavior—service graphs, circular dependencies, scopes, and lifecycle hooks—within a coherent, understandable domain that can be tested for its ability to handle various loads and for security vulnerabilities.

## Roadmap

See [GitHub Issues with "roadmap" label](https://github.com/makes3nse/banking-cli/issues?q=label%3Aroadmap)

- [x] In-Memory storage
- [x] JSON local storage
- [ ] PostgreSQL
- [ ] TOMCAT
- [ ] SERVLET
- [ ] Complete Web Support
- [ ] Multilang support
- [ ] Docker containerization
- [ ] Rate Limiting
