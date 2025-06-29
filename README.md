# Influent #

> visual analytics for big data transaction flow

Influent is a web-based application for visually and interactively following transaction flow, revealing actors and behaviors of potential concern that might otherwise go unnoticed. Summary visualization of transactional patterns and actor characteristics, interactive link expansion and dynamic entity clustering enable Influent to operate effectively at scale with big data sources, in any modern web browser. Influent has been used to explore data sets with millions of entities and hundreds of millions of transactions.

![Influent example with public Kiva data set](https://raw.github.com/unchartedsoftware/wiki-assets/master/influent/influent-kiva.png)

Service Provider Interfaces (SPIs) provide a plugin style framework for developers to provide runtime-injected modules for search, data access, clustering and other services. [Avro](http://avro.apache.org/) is used to define the SPI protocols in cross-language form in [influent-spi](influent-spi/src/main/avro). In process Java service implementations are injected via [Guice](https://code.google.com/p/google-guice/), which may optionally delegate to out of process providers using web services standards such as REST, for which Avro provides convenient serialization.

## Getting Started ##

Documentation on installing, configuring and using Influent is available in the [docs folder](https://github.com/unchartedsoftware/influent/tree/master/docs/src/) in the source code.

- Explore [live demos](http://community.influent.org/demos/) at the Influent web page.
- Review and install the necessary [prerequisites](http://community.influent.org/community/developer-docs/how-to/installation/#prerequisites) before installing Influent on your machine.
- Learn how to [build](http://community.influent.org/community/developer-docs/how-to/installation/#install-source-code) Influent from the source code.

## Architecture Documentation ##

Comprehensive cognitive architecture documentation with interactive Mermaid diagrams is available in the [architecture documentation](docs/architecture/) folder:

- **[Architecture Overview](docs/architecture/README.md)** - Complete system cognitive architecture with neural-symbolic integration patterns
- **[Service Provider Interface](docs/architecture/spi-architecture.md)** - Hypergraph pattern encoding for extensible cognitive modules  
- **[Tile Visualization System](docs/architecture/tile-architecture.md)** - Emergent multi-scale pattern navigation architecture
- **[Client-Side Architecture](docs/architecture/client-architecture.md)** - Interactive neural-symbolic interface patterns
- **[Enhancement Framework](docs/architecture/enhancement-framework.md)** - Recursive documentation improvement pathways

The architecture documentation captures **emergent cognitive patterns** and **recursive implementation pathways** through **adaptive, hypergraph-centric visualizations** that facilitate **distributed cognition for all contributors**.

## Learning More ##

- Review the [Release Notes](https://github.com/unchartedsoftware/influent/blob/master/RELEASE_NOTES.md) to see what's new.
- Read about the complete [installation and configuration procedures](http://community.influent.org/community/developer-docs/how-to/installation/) for custom Influent deployments.
- Examine the configuration of the example applications provided with the source code:
	- [Bitcoin](https://github.com/unchartedsoftware/influent/tree/master/bitcoin/)
	- [Influent App](https://github.com/unchartedsoftware/influent/tree/master/influent-app/)
	- [Kiva](https://github.com/unchartedsoftware/influent/tree/master/kiva/)
	- [Walker](https://github.com/unchartedsoftware/influent/tree/master/walker/)
- Learn how to [deploy](http://community.influent.org/community/developer-docs/how-to/deployment/) your Influent application to a web server.
- Browse the [User Guide](http://community.influent.org/docs/user-guide/) to understand how to navigate the user interface and investigate your data.

## Contact ##

For support questions, technical suggestions and contributions, please post your feedback to the [GitHub Issues forum](https://github.com/unchartedsoftware/influent/issues) for this project.

## License ##

Influent is under ongoing development and is freely available for download under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) open source licensing.