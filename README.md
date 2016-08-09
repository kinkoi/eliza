# Symphony Eliza Bot

This bot is able to some simple questions. You will interact with Eliza by starting the message with "Eliza". We include a database with some facts. For example, you can type: Eliza, tell me a fact. And she will respond with some fact. 

Bussiness usage:
1. We can replace the fun fact to company fact so employee can get answer about the company.
2. Allow employee to ask some questions to HR etc.  





The java command runs and exit, there is no daemon running and waiting for incoming messages.

## Example
```

git clone https://github.com/kinkoi/eliza.git
cd eliza
mvn clean package

java \
-Dagent.url=https://corporate-api.symphony.com:8444/agent \
-Dbot.user.email=bot.user13@symphony.com \
-Dbot.user.name=bot.user13 \
-Dcerts.dir=/Users/kinkoi.lo/dev/bot/ \
-Dkeyauth.url=https://corporate-api.symphony.com:8444/keyauth \
-Dkeystore.password=XXXX \
-Dpod.url=https://corporate-api.symphony.com:8444/pod \
-Dsessionauth.url=https://corporate-api.symphony.com:8444/sessionauth \
-Dtruststore.file=/Users/kinkoi.lo/dev/bot/truststore.ts \
-Dtruststore.password=XXXX -Droom.stream=ROOM_ID \
-Dbot.user.email=bot.user13@symphony.com

```

## Libraries
- [Symphony Java Client](https://github.com/symphonyoss/symphony-java-client)

