<script setup>
import { Authenticator } from "@aws-amplify/ui-vue";
import { Amplify } from "aws-amplify";
import "@aws-amplify/ui-vue/styles.css";
import { post } from "aws-amplify/api";
import { fetchAuthSession } from "aws-amplify/auth";

const currentConfig = Amplify.getConfig();
Amplify.configure({
  ...currentConfig,
  API: {
    REST: {
      RentalNinja: {
        endpoint: "https://api.rentalninja.link",
        region: "us-east-2",
      },
    },
  },
});

async function get_url() {
  try {
    const { accessToken, idToken } = (await fetchAuthSession()).tokens ?? {};
    console.log(accessToken);
    const restOperation = post({
      apiName: "RentalNinja",
      path: "/get-presigned",
      options: {
        headers: { Authorization: idToken },
        body: { haha: 111 },
      },
    });
    console.log("test" + restOperation);
  } catch (e) {
    // console.log("GET call failed: ", JSON.parse(e.response.body));
  }
}

async function get_token() {}
</script>

<template>
  <authenticator>
    <template v-slot="{ user, signOut }">
      <h1>Hello {{ user.username }}!</h1>
      <button @click="signOut">Sign Out</button>
      <button @click="get_url">get url</button>
      <button @click="get_token">get token</button>
    </template>
  </authenticator>
</template>
