<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
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

[comment]: <> ([![Contributors][contributors-shield]][contributors-url])

[comment]: <> ([![Forks][forks-shield]][forks-url])

[comment]: <> ([![Stargazers][stars-shield]][stars-url])

[comment]: <> ([![Issues][issues-shield]][issues-url])

[comment]: <> ([![MIT License][license-shield]][license-url])

[comment]: <> ([![LinkedIn][linkedin-shield]][linkedin-url])



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="rc_banner.png">
    <img src="rc_banner.png" alt="Logo" width="512">
  </a>

<h3 align="center">Reconnect [Velocity]</h3>

  <p align="center">
    Reconnect your players!
    <br />
    <a href="https://github.com/Matt-MX/ReconnectVelocity/releases"><strong>Download »</strong></a>
    <br />
    <br />
    <a href="https://github.com/Matt-MX/ReconnectVelocity/issues">Report Bug</a>
    ·
    <a href="https://github.com/Matt-MX/ReconnectVelocity/issues">Request Feature</a>
  </p>
</div>

<!-- ABOUT THE PROJECT -->
## Features

[comment]: <> ([![Product Name Screen Shot][product-screenshot]]&#40;https://example.com&#41;)

With this plugin, allow users to reconnect to the last server they were logged into!

<p align="right">(<a href="#top">back to top</a>)</p>

### Setup

Firstly, head over to [the release page](https://github.com/Matt-MX/ReconnectVelocity/releases) and
download the latest version of the plugin. Drag the downloaded jar file into your
plugins folder in your Velocity server.


<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Simply alter anything in the `config.yml` that is generated located in `./plugins/reconnect/config.yml`.

To allow users to reconnect, make sure they have the permission `velocity.reconnect`!

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- Storage -->
## Storage

In the `config.yml`, we have added different storage options. If you don't need your 
own plugins to interact with this one, we recommend you keep the `method` set as the default
option (`yaml`).

```yml
storage:
  method: "yaml"
  # The below is only needed if you are using MySQL
  data:
    address: localhost
    database: reconnect
    username: root
    password: ''
```

To change the method, alter the `method` key. Current options are:

- `yaml` - Local .yml file
- `sqlite` - Local SQL database
- `mysql` - Local or remote SQL database

The `data` section is for if you are using the `mysql` option for `method`.

- `address` - The Address of the SQL database
- `database` - The name of the database
- `username` - Username for the SQL database
- `password` - Password for the SQL database

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the MIT License.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

MattMX - [MattMX#0033](https://discord.gg)

Project Link: [https://github.com/Matt-MX/AnnouncerVelocity](https://github.com/Matt-MX/AnnouncerVelocity)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* [Velocity](https://velocitypowered.com/)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[comment]: <> ([contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge)

[comment]: <> ([contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors)

[comment]: <> ([forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge)

[comment]: <> ([forks-url]: https://github.com/othneildrew/Best-README-Template/network/members)

[comment]: <> ([stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge)

[comment]: <> ([stars-url]: https://github.com/othneildrew/Best-README-Template/stargazers)

[comment]: <> ([issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge)

[comment]: <> ([issues-url]: https://github.com/othneildrew/Best-README-Template/issues)

[comment]: <> ([license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge)

[comment]: <> ([license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt)

[comment]: <> ([linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555)

[comment]: <> ([linkedin-url]: https://linkedin.com/in/othneildrew)

[comment]: <> ([product-screenshot]: images/screenshot.png)
