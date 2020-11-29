import React from 'react';

import {
    Switch,
    Route,
    BrowserRouter
} from "react-router-dom";

import SignInPage from "./components/pages/SignInPage";
import SignUpPage from "./components/pages/SignUpPage";
import NotFoundPage from "./components/pages/nonIdealStates/NotFoundPage";
import WelcomePage from "./components/pages/WelcomePage";

import {
    ApolloClient,
    ApolloLink,
    ApolloProvider,
    concat,
    HttpLink,
    InMemoryCache
} from "@apollo/client";

const httpLink = new HttpLink({ uri: process.env.NODE_ENV === 'production' ? '/graphql' : 'http://localhost:8080/graphql', useGETForQueries: false });
const authMiddleware = new ApolloLink((operation, forward) => {
    const token = localStorage.getItem('token');
    operation.setContext({
        headers: {
            authorization: token && "Bearer " + token,
        }
    });

    return forward(operation);
})

export const apolloClient = new ApolloClient({
    cache: new InMemoryCache(),
    link: concat(authMiddleware, httpLink),
});

export default () => {
    return <ApolloProvider client={apolloClient}>
        <BrowserRouter>
            <Switch>
                <Route exact path="/" component={WelcomePage}/>
                <Route exact path="/sign/in" component={SignInPage}/>
                <Route exact path="/sign/up" component={SignUpPage}/>
                <Route exact component={NotFoundPage}/>
            </Switch>
        </BrowserRouter>
    </ApolloProvider>
};
