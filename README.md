# memory
![Build](https://github.com/trevorism/memory/actions/workflows/deploy.yml/badge.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/trevorism/memory)
![GitHub language count](https://img.shields.io/github/languages/count/trevorism/memory)
![GitHub top language](https://img.shields.io/github/languages/top/trevorism/memory)

Store data into Google Cloud Storage.

Storage is tenant scoped. The bucket is chosen from the tenant claim on the caller's token:
callers without a tenant use `memory-trevorism`, a tenant uses `memory-<tenant-guid>`. A tenant's
bucket is created on its first write, so no provisioning step is needed.

# How to build
`gradle clean build`
