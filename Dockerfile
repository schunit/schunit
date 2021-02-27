FROM alpine:3.13 AS fetch

ADD schunit-cli/target /schunit-cli

RUN mkdir -p /files/usr/lib/schunit /files/usr/bin \
 && tar xzf $(ls /schunit-cli/*.tar.gz | head -1) -C /files/usr/lib/schunit \
 && chmod a+x /files/usr/lib/schunit/bin/* \
 && ln -s /usr/lib/schunit/bin/schunit8.sh /files/usr/bin/schunit

ADD COPYING /files/COPYING



FROM adoptopenjdk:8u282-b08-jre-hotspot-focal AS schunit

COPY --from=fetch /files /

WORKDIR /work

ENTRYPOINT ["schunit"]



FROM schunit AS devcontainer

# Install extra tooling
RUN apt update \
 && DEBIAN_FRONTEND=noninteractive apt install -y --no-install-recommends make git \
 && DEBIAN_FRONTEND=noninteractive apt install -y --no-install-recommends apt-utils dialog \
 && DEBIAN_FRONTEND=noninteractive apt install -y openssh-server openssh-client less iproute2 procps lsb-release \
 #
 # Cleaning up
 && apt autoremove -y \
 && rm -rf /var/lib/apt/lists/* \
 && find /tmp -mindepth 1 -maxdepth 1 | xargs rm -rf