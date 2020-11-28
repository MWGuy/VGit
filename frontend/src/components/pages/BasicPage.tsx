import React from "react";
import {HTMLDivProps} from "@blueprintjs/core";
import NavigationBar from "../NavigationBar";

export default (props: HTMLDivProps) => {
    return <div>
        <NavigationBar/>
        <div className="basic-page-content">{props.children}</div>
    </div>
}
