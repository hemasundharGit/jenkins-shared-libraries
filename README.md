# 🔧 Jenkins Shared Libraries

> **A production-ready collection of reusable Groovy pipeline steps — write your CI/CD logic once, use it across every Jenkins pipeline.**

[![Jenkins](https://img.shields.io/badge/Jenkins-Shared_Library-D24939?style=flat-square&logo=jenkins&logoColor=white)](https://github.com/hemasundharGit/jenkins-shared-libraries)
[![Groovy](https://img.shields.io/badge/Groovy-Pipeline_DSL-4298B8?style=flat-square&logo=apachegroovy&logoColor=white)](https://groovy-lang.org/)
[![Docker](https://img.shields.io/badge/Docker-Automated-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![Security](https://img.shields.io/badge/Security-Trivy%20%7C%20OWASP%20%7C%20SonarQube-critical?style=flat-square)](https://github.com/hemasundharGit/jenkins-shared-libraries)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

---

## 📌 What Is This?

This repository is a **Jenkins Shared Library** — a single source of truth for all reusable CI/CD pipeline steps. Instead of repeating the same Docker build, security scan, and quality check logic across every project's `Jenkinsfile`, define it here once and call it anywhere.

```
The DRY Principle applied to CI/CD pipelines.
```

**Without Shared Libraries:**
```
Project A Jenkinsfile → 140 lines
Project B Jenkinsfile → 138 lines   ← same code repeated
Project C Jenkinsfile → 145 lines   ← same code repeated again
Bug fix? Edit every file. New step? Add to every file. 😤
```

**With Shared Libraries:**
```
Project A Jenkinsfile → 20 lines  ✅
Project B Jenkinsfile → 18 lines  ✅
Project C Jenkinsfile → 22 lines  ✅
Bug fix? Edit ONE file. Done. 🚀
```

---

## 📁 Repository Structure

```
jenkins-shared-libraries/
│
└── vars/                              # All reusable pipeline steps (Groovy)
    ├── code_checkout.groovy           # Git source code checkout
    ├── docker_build.groovy            # Docker image build
    ├── docker_push.groovy             # Push image to Docker registry
    ├── docker_compose.groovy          # Docker Compose operations
    ├── docker_cleanup.groovy          # Post-build Docker image cleanup
    ├── owasp_dependency.groovy        # OWASP dependency vulnerability check
    ├── sonarqube_analysis.groovy      # SonarQube static code analysis
    ├── sonarqube_code_quality.groovy  # SonarQube quality gate enforcement
    └── trivy_scan.groovy              # Trivy container security scan
```

> 💡 Each `.groovy` filename = the callable step name in your `Jenkinsfile`.

---

## ⚡ Shared Steps Reference

### 🔵 Source Control

| Step | File | What It Does |
|------|------|--------------|
| `code_checkout` | `vars/code_checkout.groovy` | Parameterised Git checkout with branch & credentials support |

### 🐳 Docker

| Step | File | What It Does |
|------|------|--------------|
| `docker_build` | `vars/docker_build.groovy` | Builds a Docker image from a `Dockerfile` with custom tagging |
| `docker_push` | `vars/docker_push.groovy` | Authenticates and pushes image to DockerHub / private registry |
| `docker_compose` | `vars/docker_compose.groovy` | Runs `docker compose up/down` for multi-container setups |
| `docker_cleanup` | `vars/docker_cleanup.groovy` | Prunes dangling images & containers post-build to save disk space |

### 🔐 Security & Quality

| Step | File | What It Does |
|------|------|--------------|
| `owasp_dependency` | `vars/owasp_dependency.groovy` | Scans project dependencies for known CVEs via OWASP Dependency-Check |
| `sonarqube_analysis` | `vars/sonarqube_analysis.groovy` | Triggers SonarQube static code analysis scan |
| `sonarqube_code_quality` | `vars/sonarqube_code_quality.groovy` | Waits for and enforces SonarQube Quality Gate (fails build if gate fails) |
| `trivy_scan` | `vars/trivy_scan.groovy` | Scans Docker image for OS & library vulnerabilities using Trivy |

---

## 🚀 Setup Guide

### Step 1 — Register Library in Jenkins

Navigate to: **Manage Jenkins → Configure System → Global Pipeline Libraries**

| Field | Value |
|-------|-------|
| **Name** | `jenkins-shared-library` |
| **Default Version** | `main` |
| **SCM** | Git |
| **Repository URL** | `https://github.com/hemasundharGit/jenkins-shared-libraries.git` |

### Step 2 — Use in Your Jenkinsfile

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any

    environment {
        IMAGE_NAME = "myapp"
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
        REGISTRY   = "docker.io/yourusername"
    }

    stages {

        stage('Code Checkout') {
            steps {
                code_checkout(
                    branch: 'main',
                    url: 'https://github.com/your-org/your-repo.git'
                )
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sonarqube_analysis(
                    projectKey: 'my-app',
                    projectName: 'My Application'
                )
            }
        }

        stage('Quality Gate') {
            steps {
                sonarqube_code_quality()
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                owasp_dependency()
            }
        }

        stage('Docker Build') {
            steps {
                docker_build(
                    imageName: "${IMAGE_NAME}",
                    imageTag: "${IMAGE_TAG}"
                )
            }
        }

        stage('Trivy Image Scan') {
            steps {
                trivy_scan(
                    imageName: "${IMAGE_NAME}:${IMAGE_TAG}"
                )
            }
        }

        stage('Docker Push') {
            steps {
                docker_push(
                    imageName: "${IMAGE_NAME}",
                    imageTag: "${IMAGE_TAG}",
                    registry: "${REGISTRY}"
                )
            }
        }

        stage('Docker Compose Deploy') {
            steps {
                docker_compose(action: 'up')
            }
        }

    }

    post {
        always {
            docker_cleanup()
        }
    }
}
```

---

## 🔄 Full CI/CD Pipeline Flow

```
Code Push
    │
    ▼
┌─────────────────┐
│  code_checkout  │  ← Pull latest code from Git
└────────┬────────┘
         │
         ▼
┌──────────────────────────┐
│  sonarqube_analysis      │  ← Static code analysis
│  sonarqube_code_quality  │  ← Enforce quality gate
└────────┬─────────────────┘
         │
         ▼
┌─────────────────────┐
│  owasp_dependency   │  ← Scan dependencies for CVEs
└────────┬────────────┘
         │
         ▼
┌──────────────┐
│ docker_build │  ← Build container image
└──────┬───────┘
       │
       ▼
┌─────────────┐
│ trivy_scan  │  ← Scan image for vulnerabilities
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ docker_push │  ← Push to registry
└──────┬──────┘
       │
       ▼
┌────────────────┐
│ docker_compose │  ← Deploy multi-container app
└──────┬─────────┘
       │
       ▼
┌────────────────┐
│ docker_cleanup │  ← Clean up dangling images
└────────────────┘
```

---

## 🛠️ Tech Stack

![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=jenkins&logoColor=white)
![Groovy](https://img.shields.io/badge/Groovy-4298B8?style=flat-square&logo=apachegroovy&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?style=flat-square&logo=sonarqube&logoColor=white)
![OWASP](https://img.shields.io/badge/OWASP-000000?style=flat-square&logo=owasp&logoColor=white)
![Trivy](https://img.shields.io/badge/Trivy-1904DA?style=flat-square&logoColor=white)

---

## 📝 Blog Writeup

Full step-by-step breakdown of how this was built and configured:

**👉 [Read on Hashnode →](https://hashnode.com/@hemasundharamkolla)**

Part of my **6-Day DevOps Build Sprint** — shipping 1 real project every day.

---

## 👨‍💻 Author

**Hemasundharam Kolla**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/kollahemasundharam9)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/hemasundharGit)
[![Hashnode](https://img.shields.io/badge/Blog-2962FF?style=flat-square&logo=hashnode&logoColor=white)](https://hashnode.com/@hemasundharamkolla)

---

⭐ **Star this repo** if it saved you from Jenkinsfile chaos!
