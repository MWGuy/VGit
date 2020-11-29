import React from "react";
import {NonIdealState} from "@blueprintjs/core";
import BasicPage from "../BasicPage";

export default () => {
    return <BasicPage>
        <NonIdealState title={404} description="Not found" icon="error"/>
    </BasicPage>
}
