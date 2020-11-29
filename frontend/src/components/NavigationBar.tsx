import React, {useState} from "react";
import {
    Alignment,
    Button,
    ButtonGroup,
    Classes,
    Menu, MenuDivider, MenuItem,
    Navbar,
    NavbarGroup,
    NavbarHeading, Popover,
    Spinner
} from "@blueprintjs/core";

import {useHistory} from "react-router-dom";
import {gql, useQuery} from "@apollo/client";
import EmailAvatar from "./EmailAvatar";

import logo from "../images/logoNoText.svg"
import {apolloClient} from "../App";
import AuthorizeOptions from "./AuthorizeOptions";

const GET_CURRENT_USER = gql`
    query {
        userMe {
            email
            userName
        }
    }
`

const USER_SIGN_OUT = gql`
    mutation {
        deleteToken
    }
`

function NavigationBarProfileIndicator() {
    const history = useHistory();
    const { data, error, loading } = useQuery(GET_CURRENT_USER, {
        fetchPolicy: "no-cache"
    });
    const [loadingLogout, setLoadingLogout] = useState<boolean>(false);

    if (loading || loadingLogout) {
        return <Spinner size={24}/>
    }

    if (error) {
        return <AuthorizeOptions/>
    }

    if (data) {
        const menu = <Menu>
            <MenuItem icon="cog" text="Settings"/>
            <MenuDivider />
            <MenuItem
                icon="log-out"
                text="Log Out"
                onClick={() => {
                    setLoadingLogout(true)
                    apolloClient.mutate({
                        mutation: USER_SIGN_OUT
                    }).then(() => {
                        localStorage.removeItem("token");
                        history.push("/sign/in");
                    });
                }}
            />
        </Menu>

        return <Popover content={menu}>
            <Button
                icon={<EmailAvatar size={24} email={data.userMe.email}/>}
                className={Classes.MINIMAL}
            >{data.userMe.userName}</Button>
        </Popover>
    }

    return <div/>
}

export default () => {
    const history = useHistory();

    return <Navbar>
        <NavbarGroup align={Alignment.LEFT}>
            <NavbarHeading>
                <Button
                    icon={<img width={24} alt="logo" src={logo} />}
                    className={Classes.MINIMAL}
                    onClick={() => history.push("/")}
                >
                    <b>VGit</b>
                </Button>
            </NavbarHeading>
        </NavbarGroup>
        <NavbarGroup align={Alignment.RIGHT}>
            <NavigationBarProfileIndicator/>
        </NavbarGroup>
    </Navbar>
}
