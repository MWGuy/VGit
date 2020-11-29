import React from "react";
import {useHistory} from "react-router-dom";
import {Button, ButtonGroup, Classes} from "@blueprintjs/core";

export default () => {
    const history = useHistory();
    return <ButtonGroup>
        <Button
            className={Classes.OUTLINED}
            icon="log-in"
            text="Sign In"
            onClick={() => history.push("/sign/in")}
        />
        <Button
            className={Classes.OUTLINED}
            icon="hand"
            text="Sign Up"
            onClick={() => history.push("/sign/up")}
        />
    </ButtonGroup>
}
