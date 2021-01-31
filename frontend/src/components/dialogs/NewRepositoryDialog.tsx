import React, {useState} from "react";
import {
    Button, ButtonGroup, Callout,
    Classes,
    Dialog,
    FormGroup,
    IDialogProps,
    InputGroup,
    Intent,
    Radio,
    RadioGroup, Spinner, TextArea
} from "@blueprintjs/core";
import {gql, useMutation, useQuery} from "@apollo/client";
import {useHistory} from "react-router-dom";

const CREATE_NEW_REPOSITORY_MUTATION = gql`
    mutation CreateNewRepository($input: CreateRepositoryInput!) {
        createRepository(input: $input) {
            id
        }
    }
`

const GET_USER_INFO = gql`
    query {
        userMe {
            userName
        }
    }
`

function handleSubmit() {
    return (event: any) => {
        event.preventDefault();

    }
}

export default (props: IDialogProps) => {
    const [repositoryName, setRepositoryName] = useState<string>("");
    const [accessPermissions, setAccessPermissions] = useState<"INTERNAL" | "PUBLIC" | "PRIVATE">("INTERNAL");
    const [description, setDescription] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | undefined>(undefined);
    const [createRepository] = useMutation(CREATE_NEW_REPOSITORY_MUTATION);
    const userInfo = useQuery<{ userMe: { userName: string } }>(GET_USER_INFO);
    const history = useHistory();

    return <Dialog icon="git-repo" title="New repository" {...props}>
        {!userInfo.loading && userInfo.data?.userMe ? <form onSubmit={handleSubmit()}>
            <div className={Classes.DIALOG_BODY}>
                {error && <Callout
                    intent={Intent.DANGER}
                    onClick={() => setError(undefined)}
                    style={{ marginBottom: 8 }}
                >{error}</Callout>}
                <FormGroup
                    disabled={loading}
                    label={"Repository name"}
                    labelInfo={"(required)"}
                >
                    <InputGroup
                        placeholder="Repository name..."
                        disabled={loading}
                        value={repositoryName}
                        onChange={e => setRepositoryName(e.target.value)}
                    />
                </FormGroup>
                <RadioGroup
                    disabled={loading}
                    label="Repository visibility"
                    onChange={e => setAccessPermissions(e.currentTarget.value as "INTERNAL" | "PUBLIC" | "PRIVATE")}
                    selectedValue={accessPermissions}
                >
                    <Radio label="Internal" value="INTERNAL"/>
                    <Radio label="Public" value="PUBLIC"/>
                    <Radio label="Private" value="PRIVATE"/>
                </RadioGroup>
                <FormGroup
                    disabled={loading}
                    label={"Description"}
                    labelInfo={"(required)"}
                >
                    <TextArea
                        fill
                        disabled={loading}
                        onChange={event => setDescription(event.target.value)}
                        value={description}
                    />
                </FormGroup>
            </div>
            <div className={Classes.DIALOG_FOOTER + " " + Classes.DIALOG_FOOTER_ACTIONS}>
                <Button disabled={loading} onClick={props.onClose}>Close</Button>
                <Button
                    intent={Intent.PRIMARY}
                    disabled={loading || repositoryName.length === 0}
                    loading={loading}
                    type="submit"
                    onClick={() => {
                        setLoading(true);
                        createRepository({
                            variables: {
                                input: {
                                    accessPermission: accessPermissions,
                                    description: description,
                                    path: {
                                        namespace: userInfo.data?.userMe.userName,
                                        name: repositoryName
                                    }
                                }
                            }
                        }).then(data => {
                            setLoading(false);
                            history.push("/" + userInfo.data?.userMe.userName + "/" + repositoryName);
                        }).catch(error => {
                            setLoading(false);
                            setError(error.message);
                        });
                    }}
                >
                    Create new repository
                </Button>
            </div>
        </form> : <Spinner/>}
    </Dialog>
}
