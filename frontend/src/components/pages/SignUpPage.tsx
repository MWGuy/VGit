import React from "react";
import CredentialsInputPage from "./CredentialsInputPage";
import {Redirect} from "react-router-dom";

export default () => {
    if (localStorage.getItem("token")) {
        return <Redirect to="/"/>
    }

    return <CredentialsInputPage description="Create new account">
        I guess I should implement this, but I`m very lazy :^)
    </CredentialsInputPage>
}
