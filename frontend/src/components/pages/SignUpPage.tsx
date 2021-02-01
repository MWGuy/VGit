import React, {useState} from "react";
import CredentialsInputPage from "./CredentialsInputPage";
import {Redirect, useHistory} from "react-router-dom";
import {Button, ButtonGroup, Callout, FormGroup, InputGroup, Intent} from "@blueprintjs/core";
import {gql} from "@apollo/client";
import {apolloClient} from "../../App";

const REGISTER_USER = gql`
    mutation($info: UserInfoInput!, $credentials: AuthorizationCredentialsInput!) {
        registerUser(info: $info, credentials: $credentials) {
            token
        }
    }
`

interface SignUpHandleOptions {
    history: any;

    userName: string;
    realName: string;
    password: string;
    email: string;

    setError(error: string | undefined): void;
    setLoading(loading: boolean): void;
}

function handleSubmit(options: SignUpHandleOptions, event: any) {
        event.preventDefault();
        apolloClient.mutate({
            mutation: REGISTER_USER,
            variables: {
                info: {
                    email: options.email,
                    realName: options.realName
                },
                credentials: {
                    userName: options.userName,
                    password: options.password
                }
            }
        }).then(response => {
            options.setError(undefined);
            localStorage.setItem("token", response.data.registerUser.token);
            options.history.push("/");
        }).catch(error => {
            options.setError(error.toString());
            options.setLoading(false);
        });
    
}

export default () => {
    const history = useHistory();
    const [error, setError] = useState<string | undefined>(undefined);
    const [userName, setUserName] = useState<string>("");
    const [realName, setRealName] = useState<string>("");
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);

    if (localStorage.getItem("token")) {
        return <Redirect to="/"/>
    }

    return <CredentialsInputPage
        description="Create new account"
        callout={error && <Callout
            intent={Intent.DANGER}
            onClick={() => setError(undefined)}
        >{error}</Callout>}
    >
        <form onSubmit={console.debug}>
            <FormGroup>
                <InputGroup
                    disabled={loading}
                    placeholder="Login"
                    value={userName}
                    onChange={(event: any) => setUserName(event.target.value)}
                />
            </FormGroup>
            <FormGroup>
                <InputGroup
                    disabled={loading}
                    placeholder="Real name"
                    value={realName}
                    onChange={(event: any) => setRealName(event.target.value)}
                />
            </FormGroup>
            <FormGroup>
                <InputGroup
                    disabled={loading}
                    placeholder="Email"
                    type="email"
                    value={email}
                    onChange={(event: any) => setEmail(event.target.value)}
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
                    loading={loading}
                    intent={Intent.SUCCESS}
                    type="submit"
                    onClick={(e: any) => {
                        setLoading(true);
                        handleSubmit({
                            history, userName, realName, password, email, setError, setLoading
                        }, e)
                    }}
                >Sign Up</Button>
            </ButtonGroup>
        </form>
    </CredentialsInputPage>
}
