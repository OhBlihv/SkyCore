# SkyCore

---

#### Core System for Skytonia Plugins

---

## Notice

This project is currently private on GitHub within the Skytonia Organisation.

Please respect this decision by not sharing this project via link or source without explicit permission from the OhBlihv (Project Lead)

Contact [dev@ohblihv.me](mailto:dev@ohblihv.me)
> This email is available for project requests and other inquiries.

## How To Build

This project uses Maven to handle dependencies and the project lifecycle.

Install [Maven 3](http://maven.apache.org/download.html)

* Clone this project `git clone https://github.com/Skytonia/{project}.git
* If the project contains any compiled jars, they will use the environment variable `SKYTONIA_DEV`
* Adding this environment variable for a folder containing /lib/ with compiled jars will allow this project to compile too.
* Compile using `mvn clean package install`
