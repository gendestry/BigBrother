import { useMemo } from "react";
import { useLiveUserList } from "../../../components/hooks.ts/liveUserList";
import { Badge, Button, Group, LoadingOverlay, Table } from "@mantine/core";
import { IconCheck, IconTrash } from "@tabler/icons-react";
import { supabaseClient } from "../../../supabase/supabaseClient";




function UserList() {
    const { users, loading, error, updateUsers } = useLiveUserList();

    const toggleCheck = (id: number) => {
        supabaseClient.from("users")
            .update({ confirmed: true })
            .eq("id", id)
            .then(() => {
                updateUsers();
            });
    };

    const getUserRows = useMemo(() => {
        if(users === undefined) return;

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
                        <Button color="blue" variant="light" size="xs" radius="sm" onClick={() => {toggleCheck(user.id)}}><IconCheck/></Button>
                        <Button color="red" variant="light" size="xs" radius="sm"><IconTrash /></Button>
                    </Group>
                </Table.Td>
            </Table.Tr>
        ));
    }, [users]);


    return (
        <>
            <LoadingOverlay visible={loading} />
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