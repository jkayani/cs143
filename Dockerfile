FROM ubuntu:latest as base
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get -y install wget flex bison build-essential csh openjdk-8-jdk libxaw7-dev
WORKDIR /usr/class/

FROM base as spim
RUN apt-get install unzip
RUN wget -O spim.zip https://sourceforge.net/code-snapshots/svn/s/sp/spimsimulator/code/spimsimulator-code-r739.zip
RUN unzip spim.zip -d spim && cd spim/spimsimulator-code-r739/spim && make -e EXCEPTION_DIR=/usr/class && touch /usr/class/exceptions.s && mv spim /usr/local/bin

FROM spim as cool
RUN wget -O student-dist.tar.gz https://courses.edx.org/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block@student-dist.tar.gz
RUN tar -xf student-dist.tar.gz 
RUN wget -O pa1-grader.pl https://courses.edx.org/assets/courseware/v1/2aa4dec0c84ec3a8d91e0c1d8814452b/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block/pa1-grading.pl