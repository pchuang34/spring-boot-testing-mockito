  function showOrHideGrade(gradeType) {
        if (gradeType === "math") {
              var x = document.getElementById("mathGrade");
              if (x.style.display === "none") {
                x.style.display = "block";
              } else {
                x.style.display = "none";
              }
        }
        if (gradeType === "science") {
             var x = document.getElementById("scienceGrade");
             if (x.style.display === "none") {
                 x.style.display = "block";
            } else {
                x.style.display = "none";
            }
          }
        if (gradeType === "history") {
             var x = document.getElementById("historyGrade");
             if (x.style.display === "none") {
                 x.style.display = "block";
            } else {
                x.style.display = "none";
            }
          }
    }

    // function deleteStudent(id) {
    //     alert("delete" + id)
    //     window.location.href = "/delete/student/" + id;
    // }

    function deleteStudent(id){
    alert("delete" + id)
        fetch(`/delete/student/${id}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (response.ok) {
                    // Successful deletion
                    return response.text();
                } else {
                    // Handle error
                    throw new Error('Error deleting student');
                }
            })
            .then(data => {
                console.log(data);
                const studentsContainer = document.getElementById('students-container');
                studentsContainer.innerHTML = data;
            })
            .catch(error => {
                // Handle fetch error
                console.error(error);
            });
    }

    function deleteMathGrade(id) {
    window.location.href = "/grades/" + id + "/" + "math";
    }

    function deleteScienceGrade(id) {
    window.location.href = "/grades/" + id + "/" + "science";
    }

    function deleteHistoryGrade(id) {
    window.location.href = "/grades/" + id + "/" + "history";
    }

    function studentInfo(id) {
        window.location.href = "/studentInformation/" + id;
    }

  // function deleteMathGrade(id) {
  //     fetch("/grades/" + id + "/math", {
  //         method: "DELETE"
  //     })
  //         .then(response => {
  //             if (response.ok) {
  //                 // Handle successful deletion
  //                 console.log("Grade successfully deleted.");
  //             } else {
  //                 // Handle error
  //                 console.error("Error deleting grade:", response.status);
  //             }
  //         })
  //         .catch(error => {
  //             console.error("Error:", error);
  //         });
  // }