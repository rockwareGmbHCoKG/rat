# ![Rockware](https://rockware.info/Default-small.png)
# rat - Rockware Acl Tool for AEM

![GitHub release (latest by date)](https://img.shields.io/github/v/release/rockwareGmbHCoKG/rat)
[![Build](https://github.com/rockwareGmbHCoKG/rat/workflows/Build/badge.svg?branch=main)](https://github.com/rockwareGmbHCoKG/rat/actions?query=workflow%3ABuild+branch%3Amain)
[![codecov](https://codecov.io/gh/rockwareGmbHCoKG/rat/branch/main/graph/badge.svg)](https://codecov.io/gh/rockwareGmbHCoKG/rat)
[![CodeQL](https://github.com/rockwareGmbHCoKG/rat/workflows/CodeQL/badge.svg?branch=main)](https://github.com/rockwareGmbHCoKG/rat/actions?query=workflow%3ACodeQL)
[![License](https://img.shields.io/github/license/rockwareGmbHCoKG/rat)](https://github.com/rockwareGmbHCoKG/rat/blob/main/LICENSE)

## Another ACL Tool? Why?
There are already several ACL tools available for AEM. Some create groups and set ac properties based on YAML files, others copy rules from A to B. rat is using a different approach.

### Very short summary
You tell rat in a CaConfig which groups you would like to have. Then - whenver you add a page in AEM - rat makes sure that those groups are available throughout the content
hierarchy. It also adds AC settings, makes sure that group inheritance is working fine and creates additional folders so that your site structure is consistent.

### Walkthrough by examples
