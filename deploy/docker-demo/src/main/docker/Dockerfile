FROM centos:8.2.2004

# install tools
RUN yum install wget -y \
 && wget -O /etc/yum.repos.d/epel-7.repo  http://mirrors.aliyun.com/repo/epel-7.repo \ 
 && yum install java net-tools  nc crontabs expect unzip -y \
 && yum install langpacks-zh_CN.noarch -y \
 && echo "LANG=zh_CN.utf8" >> /etc/locale.conf \
 && source /etc/locale.conf \
 && yum clean all

WORKDIR /export/jdchain
COPY zip/* /export/jdchain/

# env
ENV RELEASE_DIR=/export/jdchain
ENV RELEASE_VERSION=1.3.0
#ENV DATA_DIR=/shared
ENV NAME=conf
ENV SERVER_NAME_PEER=deploy-peer
ENV SERVER_NAME_GW=deploy-gateway

COPY script/* /export/jdchain/
RUN chmod +x /export/jdchain/*.sh

# ports
EXPOSE 8080
#EXPOSE 16000
#EXPOSE 16010
#EXPOSE 16020
#EXPOSE 16030

#ENTRYPOINT ["/bin/sh","-c","/export/jdchain/start.sh"]
ENTRYPOINT sh /export/jdchain/start.sh

