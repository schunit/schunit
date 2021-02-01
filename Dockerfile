FROM alpine:3.13 AS fetch

ADD schunit-cli/target /schunit-cli

RUN mkdir -p /files/usr/lib/schunit /files/usr/bin \
 && tar xzf $(ls /schunit-cli/*.tar.gz | head -1) -C /files/usr/lib/schunit \
 && chmod a+x /files/usr/lib/schunit/bin/* \
 && ln -s /usr/lib/schunit/bin/schunit8.sh /files/usr/bin/schunit

ADD COPYING /files/COPYING


FROM adoptopenjdk:8u282-b08-jre-hotspot-focal

COPY --from=fetch /files /

WORKDIR /work

ENTRYPOINT ["schunit"]

