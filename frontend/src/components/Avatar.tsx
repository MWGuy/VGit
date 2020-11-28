import React from "react";

export interface AvatarProps extends React.ImgHTMLAttributes<HTMLImageElement> {
    size: number;
}

export default (props: AvatarProps) => {
    return <img {...props} style={{
        borderRadius: "100%",
        width: props.size,
        height: props.size
    }} alt="avatar"/>
}
