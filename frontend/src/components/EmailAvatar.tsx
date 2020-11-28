import React from "react";
import Avatar, {AvatarProps} from "./Avatar";
import crypto from "crypto-js";

export interface EmailAvatarProps extends AvatarProps {
    email: string;
}

export default (props: EmailAvatarProps) => {
    return <Avatar {...props} src={"https://www.gravatar.com/avatar/" + crypto.MD5(props.email).toString() + "?default=identicon"}/>
}
