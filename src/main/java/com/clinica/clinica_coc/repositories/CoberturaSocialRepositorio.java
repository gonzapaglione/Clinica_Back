
package com.clinica.clinica_coc.repositories;

 //@author Gonzalo Lopez Paglione
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clinica.clinica_coc.models.CoberturaSocial;


 
@Repository
public interface CoberturaSocialRepositorio extends JpaRepository<CoberturaSocial, Long> {
}
