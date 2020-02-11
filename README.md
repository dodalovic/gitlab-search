<!--
*** Thanks for checking out this README Template. If you have a suggestion that would
*** make this better, please fork the repo and create a pull request or simply open
*** an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
-->

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<br />
<p align="center">
    <img src="GitLab_Logo.svg.png" alt="Logo" width="80" height="80">

  <h3 align="center">gitlab-search project</h3>

  <p align="center">
    Simple API server providing capability to search through given Gitlab instance for any arbitrary text
  </p>
</p>



<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)

## About The Project

The scope of the project is to simplify searching through (mostly self hosted) instances of Gitlab. Main use cases are searching through all the projects, or the projects satisfying some pattern.

## Getting Started

### Run docker image

```
docker run -e GITLAB_API=https://YOUR_SERVER_HERE/api/v4 -e GITLAB_TOKEN=XXXXXXXYYYYYYYY --rm -p 8080:8080 dodalovic/gitlab-search
```

### Querying API

```
$ curl --url 'http://localhost:8080/search?searchTerm=SOME_ARBITRARY_TEXT'--header 'accept: application/json'
```

<!-- USAGE EXAMPLES -->
## Usage

API will be available via http://localhost:8080/search

**Query params**:

* `searchTerm` - mandatory, text you want to search for
* `pattern` - optional - search through projects matching given pattern

An example call:

```
curl --url 'http://localhost:8080/search?searchTerm=triggerContentCapabilities&pattern=service' \
  --header 'accept: application/json'
```

will search through all the projects containing service in their name

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

Dusan Odalovic - [@odalinjo](https://twitter.com/odalinjo) - dodalovic@gmail.com

Project Link: [https://github.com/dodalovic/gitlab-search](https://github.com/dodalovic/gitlab-search)
