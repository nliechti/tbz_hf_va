# Vertiefungsarbeit 2019 TBZ HF

![](https://travis-ci.com/nliechti/tbz_hf_va.svg?branch=master)

## [Dokumentaion der Arbeit](docs)

Die offiziell geforderte Dokumentation findet in Asciidoc statt, da dies besser als markdown zu einem PDF compiled werden kann.

Der rest ist alles auf github in Markdown dokumentiert.

* [Antrag](docs/antrag/projektAntrag.pdf)
* [Vorstudie](docs/vorstudie/vorstudie.pdf)
* [Hauptstudie](docs/hauptstudie/hauptstudie.pdf)

## [Applikation](app)

Die Applikation selber ist in [javalin](https://javalin.io) und [vue.js](https://vuejs.org) geschrieben.


### Deployment der Applikation

Die möglichen Deployment Parameter sind [hier](https://github.com/nliechti/tbz_hf_va/blob/master/docs/deploy/deployment.md) beschrieben.

Grundsätzlich kann die Applikation mit diesem [kubernetes yaml](https://github.com/nliechti/tbz_hf_va/blob/master/app/tbz-deployer.yaml) und den oben beschriebenen Parametern deployt werden.

## Benutzung der Applikation

Wie die Applikation zu benutzen ist, ist [hier](https://github.com/nliechti/tbz_hf_va/blob/master/docs/howto/howto_tbz_deployer.md) beschrieben

Wie Ressourcen für die Applikation erstellt werden können ist [hier](https://github.com/nliechti/tbz_hf_va/blob/master/docs/howto/HowToResource.md) beschrieben.

## Voraussetzungen

Die Voraussetzung sind [hier](https://github.com/nliechti/tbz_hf_va/blob/master/docs/hauptstudie/technical_prerequisites.adoc) dokumentiert
