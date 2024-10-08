import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./assets/style.css";
import { Amplify } from "aws-amplify";
import OpenLayers from 'vue3-openlayers';
import 'vue3-openlayers/dist/vue3-openlayers.css';
import 'leaflet/dist/leaflet.css';

Amplify.configure({
    Auth: {
        Cognito: {
            userPoolId: "us-east-2_0zgg97WG0",
            userPoolClientId: "3mhrk7hsqteu9r74vhvdula5fm",
            signUpVerificationMethod: "code", // 'code' | 'link'
            loginWith: {
                oauth: {
                    domain: "https://auth.rentalninja.link",
                    scopes: ["phone", "email", "openid"],
                    redirectSignIn: ["http://localhost:8080/"],
                    redirectSignOut: ["http://localhost:8080/"],
                    responseType: "code", // or 'token', note that REFRESH token will only be generated when the responseType is code
                },
            },
        },
    },
});
createApp(App).use(router).use(OpenLayers).mount("#app");