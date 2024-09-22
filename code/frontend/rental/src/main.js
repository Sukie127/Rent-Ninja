import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./assets/style.css";
import { Amplify } from "aws-amplify";
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
createApp(App).use(router).mount("#app");
