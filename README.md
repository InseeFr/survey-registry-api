# Registre API Services (Spring Boot)

This project provides two central services in the survey lifecycle management ecosystem: the **Suggester Registry** and the **Questionnaire Registry**. Both are developed in Java using Spring Boot and aim to be reliable sources of external lists and validated questionnaires across modules of the data collection and processing chain.

## ✨ Overview

### 🔍 Suggester Registry

A centralized API service to:

- Serve as the official source of external code lists and nomenclatures, searchable via parameters.
- Interface with **RMéS** to both fetch and publish code lists.
- Support **Pogues** for enabling list-based suggestions and version handling.
- Provide complete lists of modalities to other modules (e.g., Collection, Preparation, Processing), which are encouraged to maintain local caches to avoid strong runtime coupling.

Initially managed through an API, with plans for a basic admin UI.

### 📋 Questionnaire Registry

A centralized API service to:

- Serve as the official source of **validated and published questionnaires** from Pogues.
- Support versioning of questionnaires independently from simple saves.
- Enable access to questionnaires and collection instruments by other modules in the data lifecycle.
- Provide metadata access (e.g., variable scopes) for processing needs.

Administration is designed to be **more automated**, with each designer responsible for managing their own published versions (e.g., `pogues-model`, `ddi`, `lunatic-model`).

## 🚀 Tech Stack

- Java 21+
- Spring Boot
- PostgreSQL
- RESTful APIs

## 📦 Modules Integration

This service is intended to be used by various components of the data chain such as:

- Pogues (questionnaire design)
- Data collection and processing tools
- RMéS (metadata registry)

## 📄 License

[MIT](LICENSE)

---

Feel free to contribute or open issues for feedback and improvements!
