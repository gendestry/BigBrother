import { useMemo } from "react";
import { useLiveUserList } from "../../../components/hooks.ts/liveUserList";
import { ActionIcon, Badge, Button, Group, LoadingOverlay, Modal, Table, Title } from "@mantine/core";
import { IconCheck, IconTrash } from "@tabler/icons-react";
import { supabaseClient } from "../../../supabase/supabaseClient";
import { notifications } from "@mantine/notifications";
import { useDisclosure } from "@mantine/hooks";


function UserList() {
    const { users, loading, error, updateUsers } = useLiveUserList();
    // const [openedModal, { openModal, closeModal }] = useDisclosure(false);

    

    const toggleCheck = (id: number, username: string) => {
        supabaseClient.from("users")
            .select("confirmed")
            .eq("id", id)
            .then((res) => {
                if(res.error) {
                    notifications.show({ title: "Confirmation error", message: res.error.message, color: "red" });
                    return;
                }
                supabaseClient.from("users")
                    .update({ confirmed: res.data[0].confirmed ? false : true})
                    .eq("id", id)
                    .then(() => {
                        updateUsers();
                        notifications.show({ title: "User updated!", color: "green", message: `User ${username} updated successfully` });
                    });
            });
    };

    const deleteUser = (id: number, username: string ) => {
        supabaseClient.from("users")
            .delete()
            .eq("id", id)
            .then((res) => {
                if(res.error) {
                    notifications.show({ title: "Deletion error", message: res.error.message, color: "red" });
                }
                else {
                    updateUsers();
                    notifications.show({ title: "User deleted!", color: "green", message: `User ${username} deleted successfully` });
                }
            });
    }

    const getUserRows = useMemo(() => {
        return users.sort((a, b) => a.id - b.id).map((user) => (
            <Table.Tr key={user.id}>
                <Table.Td>{user.id}</Table.Td>
                <Table.Td>{user.username}</Table.Td>
                <Table.Td>{user.name}</Table.Td>
                <Table.Td>{user.surname}</Table.Td>
                <Table.Td>{user.email}</Table.Td>
                <Table.Td><Badge color={user.confirmed ? "blue" : "red"}>{user.confirmed ? "Yes" : "No"}</Badge></Table.Td>
                <Table.Td>
                    <Group justify="right">
                        <Button color="blue" variant="light" size="xs" radius="sm" onClick={() => {toggleCheck(user.id, user.username)}}><IconCheck/></Button>
                        <Button color="red" variant="light" size="xs" radius="sm" onClick={() => {deleteUser(user.id, user.username)}}><IconTrash /></Button>
                    </Group>
                </Table.Td>
            </Table.Tr>
        ));
    }, [users]);


    return (
        <>
            <LoadingOverlay visible={loading} />
            {/* <Modal opened={opened} onClose={close} withCloseButton={false}>
                Modal without header, press escape or click on overlay to close
            </Modal>
            <Button onClick={open}>Open modal</Button> */}
            <Table.ScrollContainer minWidth={800}>
                <Table stickyHeader>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>ID</Table.Th>
                            <Table.Th>Username</Table.Th>
                            <Table.Th>Name</Table.Th>
                            <Table.Th>Surname</Table.Th>
                            <Table.Th>Email</Table.Th>
                            <Table.Th>Confirmed</Table.Th>
                            {/* <Table.Th>Actions</Table.Th> */}
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>{getUserRows}</Table.Tbody>
                </Table>
            </Table.ScrollContainer>
        </>
    );
}

export default UserList;