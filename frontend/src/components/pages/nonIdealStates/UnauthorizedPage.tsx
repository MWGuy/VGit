import React from "react";
import {NonIdealState} from "@blueprintjs/core";
import BasicPage from "../BasicPage";
import AuthorizeOptions from "../../AuthorizeOptions";

export default () => {
    return <BasicPage>
        <NonIdealState
            icon="warning-sign"
            title="Unauthorized"
            description="To visit this page you must be authorized"
            action={<AuthorizeOptions/>}
        />
    </BasicPage>
}
