FROM ubuntu:latest as base
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get -y install unzip wget flex bison build-essential csh openjdk-8-jdk libxaw7-dev
WORKDIR /usr/class/cs143/

FROM base as spim
RUN wget -O student-dist.tar.gz https://courses.edx.org/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block@student-dist.tar.gz &&\
    tar -xf student-dist.tar.gz 
RUN wget -O spim.zip https://sourceforge.net/code-snapshots/svn/s/sp/spimsimulator/code/spimsimulator-code-r739.zip &&\
    unzip spim.zip -d spim &&\
    mkdir -p cool/lib &&\
    cp lib/trap.handler cool/lib &&\
    cd spim/spimsimulator-code-r739/spim &&\
    make DEFINES=-DDEFAULT_EXCEPTION_HANDLER='\"/usr/class/cs143/cool/lib/trap.handler\"' &&\
    mv spim /usr/local/bin

FROM spim as cool
RUN wget -O pa1-grader.pl https://courses.edx.org/assets/courseware/v1/2aa4dec0c84ec3a8d91e0c1d8814452b/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block/pa1-grading.pl
RUN wget -O pa2-grader.pl https://courses.edx.org/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block@pa2-grading.pl
RUN wget -O pa3-grader.pl https://courses.edx.org/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block@pa3-grading.pl
RUN wget -O pa4-grader.pl https://courses.edx.org/asset-v1:StanfordOnline+SOE.YCSCS1+1T2020+type@asset+block@pa4-grading.pl
