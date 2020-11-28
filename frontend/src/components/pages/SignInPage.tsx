import React, {useState} from "react";
import CredentialsInputPage from "./CredentialsInputPage";
import {Button, ButtonGroup, Callout, FormGroup, InputGroup, Intent} from "@blueprintjs/core";
import {apolloClient} from "../../App";
import {gql} from "@apollo/client";
import {Redirect, useHistory} from "react-router-dom";

const SIGN_IN_USER = gql`
    mutation ($username: String!, $password: String!) {
        authenticateUser(credentials: {
            userName: $username,
            password: $password
        }) {
            token
        }
    }
`

function handleSubmit(history: any, username: string,
                      password: string,
                      setLoading: (loading: boolean) => void,
                      setError: (error: string | undefined) => void) {
    return (event: any) => {
        event.preventDefault();
        apolloClient.mutate({
            mutation: SIGN_IN_USER,
            variables: {
                username, password
            }
        }).then(response => {
            setError(undefined);
            localStorage.setItem("token", response.data.authenticateUser.token);
            history.push("/");
        }).catch(error => {
            setError(error.toString());
            setLoading(false);
        });
    }
}

export default () => {
    const history = useHistory();
    const [error, setError] = useState<string | undefined>(undefined);
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);

    if (localStorage.getItem("token")) {
        return <Redirect to="/"/>
    }

    return <CredentialsInputPage
        description="Sign In to account"
        callout={error && <Callout
            intent={Intent.DANGER}
            onClick={() => setError(undefined)}
        >{error}</Callout>}
    >
        <form onSubmit={handleSubmit(history, username, password, setLoading, setError)}>
            <FormGroup>
                <InputGroup
                    disabled={loading}
                    placeholder="Login"
                    type="text"
                    value={username}
                    onChange={(event: any) => setUsername(event.target.value)}
                />
            </FormGroup>
            <FormGroup>
                <InputGroup
                    disabled={loading}
                    placeholder="Password"
                    type="password"
                    value={password}
                    onChange={(event: any) => setPassword(event.target.value)}
                />
            </FormGroup>
            <ButtonGroup fill>
                <Button
                    disabled={username.trim() === "" || password.trim() === ""}
                    loading={loading}
                    intent={Intent.SUCCESS}
                    onClick={() => setLoading(!loading)}
                    type="submit"
                >Sign In</Button>
            </ButtonGroup>
        </form>
    </CredentialsInputPage>
}
